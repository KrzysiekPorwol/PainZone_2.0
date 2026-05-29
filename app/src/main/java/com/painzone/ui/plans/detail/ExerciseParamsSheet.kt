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
private const val REST_STEP_SECONDS = 30

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

            // Sets count drives the reps list size: grow appends a fresh set, shrink drops the last.
            StepperRow(
                label = "Serie",
                value = reps.size.toString(),
                onMinus = { if (reps.size > 1) reps.removeAt(reps.size - 1) },
                minusEnabled = reps.size > 1,
                onPlus = { reps.add(reps.lastOrNull() ?: DEFAULT_REPS) },
            )

            reps.forEachIndexed { index, value ->
                StepperRow(
                    label = "Seria ${index + 1}",
                    value = value.toString(),
                    onMinus = { if (value > 1) reps[index] = value - 1 },
                    minusEnabled = value > 1,
                    onPlus = { reps[index] = value + 1 },
                )
            }

            StepperRow(
                label = "Odpoczynek",
                value = formatRest(rest),
                // Step down from 0:30 lands on "—" (null = no rest target).
                onMinus = {
                    rest = when {
                        rest == null -> null
                        rest!! <= REST_STEP_SECONDS -> null
                        else -> rest!! - REST_STEP_SECONDS
                    }
                },
                minusEnabled = rest != null,
                onPlus = { rest = (rest ?: 0) + REST_STEP_SECONDS },
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(onClick = onDismiss, enabled = !saving) { Text("Anuluj") }
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = {
                        saving = true
                        scope.launch {
                            onSave(reps.toList(), rest)
                            onDismiss()
                        }
                    },
                    enabled = !saving,
                ) { Text("Zapisz") }
            }
        }
    }
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

@Preview(showBackground = true, name = "Params form")
@Composable
private fun ExerciseParamsPreview() {
    // Sheet itself can't render in @Preview; mirror its body for visual coverage.
    PainZoneTheme {
        Surface {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text("Wyciskanie sztangi", style = MaterialTheme.typography.titleLarge)
                StepperRow("Serie", "3", {}, true, {})
                StepperRow("Seria 1", "10", {}, true, {})
                StepperRow("Seria 2", "9", {}, true, {})
                StepperRow("Seria 3", "8", {}, true, {})
                StepperRow("Odpoczynek", formatRest(90), {}, true, {})
            }
        }
    }
}