package com.painzone.ui.plans.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.painzone.ui.theme.PainZoneTheme
import kotlinx.coroutines.launch

private const val DEFAULT_REPS = 10
private const val REST_STEP_SECONDS = 15
private const val STEP_COUNT = 3

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseParamsSheet(
    exerciseName: String,
    isDeleted: Boolean,
    initialTargetReps: List<Int>,
    initialRestSeconds: Int?,
    onSave: suspend (targetReps: List<Int>, restSeconds: Int?) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    // toMutableStateList so per-set edits recompose without rebuilding the list each time.
    val reps = remember { initialTargetReps.toMutableStateList() }
    var rest by remember { mutableStateOf(initialRestSeconds) }
    var step by remember { mutableIntStateOf(0) }
    var saving by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(text = exerciseName, style = MaterialTheme.typography.titleLarge)
            if (isDeleted) {
                Text(
                    text = "To ćwiczenie zostało usunięte z biblioteki.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            Text(
                text = "Krok ${step + 1}/$STEP_COUNT — ${stepTitle(step)}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            when (step) {
                0 -> StepSets(reps)
                1 -> StepReps(reps)
                else -> StepRest(rest = rest, onRestChange = { rest = it })
            }

            WizardFooter(
                step = step,
                saving = saving,
                onBack = { if (step == 0) onDismiss() else step-- },
                onNext = { step++ },
                onSave = {
                    saving = true
                    scope.launch {
                        onSave(reps.toList(), rest)
                        onDismiss()
                    }
                },
            )
        }
    }
}

// Step 1: sets count drives the reps list size — grow appends a fresh set, shrink drops the last.
@Composable
private fun StepSets(reps: androidx.compose.runtime.snapshots.SnapshotStateList<Int>) {
    StepperRow(
        label = "Serie",
        value = reps.size.toString(),
        onMinus = { if (reps.size > 1) reps.removeAt(reps.size - 1) },
        minusEnabled = reps.size > 1,
        onPlus = { reps.add(reps.lastOrNull() ?: DEFAULT_REPS) },
    )
}

// Step 2: per-set reps, all sets shown at once.
@Composable
private fun StepReps(reps: androidx.compose.runtime.snapshots.SnapshotStateList<Int>) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        reps.forEachIndexed { index, value ->
            StepperRow(
                label = "Seria ${index + 1}",
                value = value.toString(),
                onMinus = { if (value > 1) reps[index] = value - 1 },
                minusEnabled = value > 1,
                onPlus = { reps[index] = value + 1 },
            )
        }
    }
}

// Step 3: rest between sets, 15s steps. Stepping down from 0:15 lands on "—" (null = no target).
@Composable
private fun StepRest(rest: Int?, onRestChange: (Int?) -> Unit) {
    StepperRow(
        label = "Odpoczynek",
        value = formatRest(rest),
        onMinus = {
            onRestChange(
                when {
                    rest == null -> null
                    rest <= REST_STEP_SECONDS -> null
                    else -> rest - REST_STEP_SECONDS
                },
            )
        },
        minusEnabled = rest != null,
        onPlus = { onRestChange((rest ?: 0) + REST_STEP_SECONDS) },
    )
}

@Composable
private fun WizardFooter(
    step: Int,
    saving: Boolean,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onSave: () -> Unit,
) {
    val isLast = step == STEP_COUNT - 1
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
    ) {
        TextButton(onClick = onBack, enabled = !saving) {
            Text(if (step == 0) "Anuluj" else "Wstecz")
        }
        Spacer(Modifier.width(8.dp))
        if (isLast) {
            Button(onClick = onSave, enabled = !saving) { Text("Zapisz") }
        } else {
            Button(onClick = onNext, enabled = !saving) { Text("Dalej") }
        }
    }
}

private fun stepTitle(step: Int): String = when (step) {
    0 -> "ilość serii"
    1 -> "ilość powtórzeń"
    else -> "czas odpoczynku między seriami"
}

@Composable
private fun StepperRow(
    label: String,
    value: String,
    onMinus: () -> Unit,
    minusEnabled: Boolean,
    onPlus: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
        )
        FilledTonalIconButton(onClick = onMinus, enabled = minusEnabled) {
            Icon(Icons.Filled.Remove, contentDescription = "Zmniejsz: $label")
        }
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .widthIn(min = 48.dp),
        )
        FilledTonalIconButton(onClick = onPlus) {
            Icon(Icons.Filled.Add, contentDescription = "Zwiększ: $label")
        }
    }
}

// "—" when null, otherwise m:ss (e.g. 90 -> "1:30").
internal fun formatRest(seconds: Int?): String {
    if (seconds == null) return "—"
    val mins = seconds / 60
    val secs = seconds % 60
    return "%d:%02d".format(mins, secs)
}

@Preview(showBackground = true, name = "Krok 1 — Serie")
@Composable
private fun StepSetsPreview() {
    PainZoneTheme {
        Surface {
            WizardBodyPreview(step = 0) {
                StepperRow("Serie", "3", {}, true, {})
            }
        }
    }
}

@Preview(showBackground = true, name = "Krok 2 — Powtórzenia")
@Composable
private fun StepRepsPreview() {
    PainZoneTheme {
        Surface {
            WizardBodyPreview(step = 1) {
                StepperRow("Seria 1", "10", {}, true, {})
                StepperRow("Seria 2", "9", {}, true, {})
                StepperRow("Seria 3", "8", {}, true, {})
            }
        }
    }
}

@Preview(showBackground = true, name = "Krok 3 — Odpoczynek")
@Composable
private fun StepRestPreview() {
    PainZoneTheme {
        Surface {
            WizardBodyPreview(step = 2) {
                StepperRow("Odpoczynek", formatRest(90), {}, true, {})
            }
        }
    }
}

// Sheet itself can't render in @Preview; mirror header + body + footer for visual coverage.
@Composable
private fun WizardBodyPreview(step: Int, body: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Wyciskanie sztangi", style = MaterialTheme.typography.titleLarge)
        Text(
            text = "Krok ${step + 1}/$STEP_COUNT — ${stepTitle(step)}",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        body()
        WizardFooter(step = step, saving = false, onBack = {}, onNext = {}, onSave = {})
    }
}