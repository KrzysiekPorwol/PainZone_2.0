package com.painzone.ui.plans

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
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
    var showAddDayDialog by remember { mutableStateOf(false) }

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
        onAddDayClick = { showAddDayDialog = true },
        onRemoveDay = viewModel::removeDay,
        onSave = viewModel::save,
        onBack = attemptBack,
        modifier = modifier,
    )

    if (showAddDayDialog) {
        AddDayDialog(
            existingDays = state.days,
            onConfirm = { name ->
                viewModel.addDay(name)
                showAddDayDialog = false
            },
            onDismiss = { showAddDayDialog = false },
        )
    }
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
    onAddDayClick: () -> Unit,
    onRemoveDay: (Int) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

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

            Text(
                text = "Ilość sesji treningowych w skali tygodnia",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
            )

            if (state.days.isEmpty()) {
                Text(
                    text = "Brak sesji. Dodaj pierwszą sesję przyciskiem poniżej.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                )
            } else {
                state.days.forEachIndexed { index, dayName ->
                    ListItem(
                        headlineContent = { Text(dayName) },
                        trailingContent = {
                            IconButton(onClick = { onRemoveDay(index) }) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Usuń sesję $dayName",
                                )
                            }
                        },
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
            }

            OutlinedButton(
                onClick = onAddDayClick,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            ) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Text("Sesja", modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}

@Composable
private fun AddDayDialog(
    existingDays: List<String>,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nowa sesja") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    if (error != null) error = null
                },
                label = { Text("Nazwa sesji (np. Push/Klata+biceps)") },
                singleLine = true,
                isError = error != null,
                supportingText = error?.let { { Text(it) } },
                modifier = Modifier.focusRequester(focusRequester),
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val trimmed = name.trim()
                    // Uniqueness is checked case-insensitively for friendlier UX;
                    // the plan is not persisted yet, so this is the only guard.
                    if (existingDays.any { it.equals(trimmed, ignoreCase = true) }) {
                        error = "Sesja o tej nazwie już istnieje"
                    } else {
                        onConfirm(trimmed)
                    }
                },
                enabled = name.trim().isNotEmpty(),
            ) {
                Text("Dodaj")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Anuluj") }
        },
    )
}

@Preview(showBackground = true, name = "Empty")
@Composable
private fun PlanCreateEmptyPreview() {
    PainZoneTheme {
        Surface {
            PlanCreateContent(
                state = PlanCreateUiState(),
                onNameChange = {},
                onAddDayClick = {},
                onRemoveDay = {},
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
                onAddDayClick = {},
                onRemoveDay = {},
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
                    days = listOf("Push", "Pull"),
                    nameError = "Plan o tej nazwie już istnieje",
                ),
                onNameChange = {},
                onAddDayClick = {},
                onRemoveDay = {},
                onSave = {},
                onBack = {},
            )
        }
    }
}