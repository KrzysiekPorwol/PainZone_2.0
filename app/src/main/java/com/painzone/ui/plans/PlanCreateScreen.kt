package com.painzone.ui.plans

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.painzone.ui.theme.PainZoneTheme

@Composable
fun PlanCreateScreen(
    onSaved: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlanCreateViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showDiscardDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.savedEvents.collect { onSaved() }
    }

    // Back (gesture or arrow) confirms only when there is unsaved work.
    val attemptBack = {
        if (state.isDirty) showDiscardDialog = true else onBack()
    }
    BackHandler(enabled = state.isDirty) { showDiscardDialog = true }

    PlanCreateContent(
        state = state,
        onNameChange = viewModel::onNameChange,
        onIncrementDays = viewModel::incrementDays,
        onDecrementDays = viewModel::decrementDays,
        onDayNameChange = viewModel::onDayNameChange,
        onSave = viewModel::save,
        onBack = attemptBack,
        modifier = modifier,
    )

    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text("Odrzucić zmiany?") },
            confirmButton = {
                TextButton(onClick = {
                    showDiscardDialog = false
                    onBack()
                }) {
                    Text("Odrzuć")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) {
                    Text("Anuluj")
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlanCreateContent(
    state: PlanCreateUiState,
    onNameChange: (String) -> Unit,
    onIncrementDays: () -> Unit,
    onDecrementDays: () -> Unit,
    onDayNameChange: (Int, String) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    // A session field shows its "blank" error only after the user has focused and left it —
    // so a fresh plan with empty fields doesn't greet the user with red underlines.
    val everFocused = remember { mutableStateMapOf<Int, Boolean>() }
    val touched = remember { mutableStateMapOf<Int, Boolean>() }
    val anyBlankTouched = state.days.withIndex().any { (i, name) -> name.isBlank() && touched[i] == true }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Nowy plan") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Wróć",
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onSave, enabled = state.canSave) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Zapisz plan",
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .imePadding(),
        ) {
            OutlinedTextField(
                value = state.name,
                onValueChange = onNameChange,
                label = { Text("Nazwa planu") },
                singleLine = true,
                isError = state.nameError != null,
                supportingText = state.nameError?.let { { Text(it) } },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .focusRequester(focusRequester),
            )

            DayCountStepper(
                count = state.days.size,
                onMinus = onDecrementDays,
                onPlus = onIncrementDays,
                minusEnabled = state.days.size > PlanCreateUiState.MIN_DAY_COUNT,
                plusEnabled = state.days.size < PlanCreateUiState.MAX_DAY_COUNT,
            )

            state.days.forEachIndexed { index, dayName ->
                OutlinedTextField(
                    value = dayName,
                    onValueChange = { onDayNameChange(index, it) },
                    label = { Text("Sesja ${index + 1}") },
                    placeholder = { Text("np. Push / Nogi / FBW") },
                    singleLine = true,
                    isError = dayName.isBlank() && touched[index] == true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 6.dp)
                        .onFocusChanged { focus ->
                            if (focus.isFocused) {
                                everFocused[index] = true
                            } else if (everFocused[index] == true) {
                                touched[index] = true
                            }
                        },
                )
            }

            // Blank error only surfaces after a field was touched; duplicates show immediately
            // because they only arise once the user has typed conflicting names.
            val daysError = when {
                state.hasDuplicateDay -> "Nazwy sesji muszą być unikalne"
                anyBlankTouched -> "Nazwy sesji nie mogą być puste"
                else -> null
            }
            daysError?.let { error ->
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp),
                )
            }

            // Backup save action — mirrors the ✓ in the top bar for users who scroll past it.
            Button(
                onClick = onSave,
                enabled = state.canSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
            ) {
                Text("Zapisz plan")
            }
        }
    }
}

@Composable
private fun DayCountStepper(
    count: Int,
    onMinus: () -> Unit,
    onPlus: () -> Unit,
    minusEnabled: Boolean,
    plusEnabled: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Liczba dni treningowych\nw skali tygodnia",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
        )
        FilledTonalIconButton(onClick = onMinus, enabled = minusEnabled) {
            Icon(Icons.Filled.Remove, contentDescription = "Mniej sesji")
        }
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .widthIn(min = 48.dp),
        )
        FilledTonalIconButton(onClick = onPlus, enabled = plusEnabled) {
            Icon(Icons.Filled.Add, contentDescription = "Więcej sesji")
        }
    }
}

@Preview(showBackground = true, name = "Default (3 sesje)")
@Composable
private fun PlanCreateDefaultPreview() {
    PainZoneTheme {
        Surface {
            PlanCreateContent(
                state = PlanCreateUiState(),
                onNameChange = {},
                onIncrementDays = {},
                onDecrementDays = {},
                onDayNameChange = { _, _ -> },
                onSave = {},
                onBack = {},
            )
        }
    }
}

@Preview(showBackground = true, name = "Content")
@Composable
private fun PlanCreateContentPreview() {
    PainZoneTheme {
        Surface {
            PlanCreateContent(
                state = PlanCreateUiState(
                    name = "Push/Pull/Legs",
                    days = listOf("Push", "Pull", "Legs"),
                ),
                onNameChange = {},
                onIncrementDays = {},
                onDecrementDays = {},
                onDayNameChange = { _, _ -> },
                onSave = {},
                onBack = {},
            )
        }
    }
}

@Preview(showBackground = true, name = "Duplicate name error")
@Composable
private fun PlanCreateErrorPreview() {
    PainZoneTheme {
        Surface {
            PlanCreateContent(
                state = PlanCreateUiState(
                    name = "Push/Pull/Legs",
                    days = listOf("Push", "Push"),
                    nameError = "Plan o tej nazwie już istnieje",
                ),
                onNameChange = {},
                onIncrementDays = {},
                onDecrementDays = {},
                onDayNameChange = { _, _ -> },
                onSave = {},
                onBack = {},
            )
        }
    }
}
