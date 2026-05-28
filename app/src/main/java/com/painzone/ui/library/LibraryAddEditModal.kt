package com.painzone.ui.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.painzone.domain.exercise.CreateResult
import com.painzone.domain.exercise.ExerciseUsage
import com.painzone.domain.exercise.MuscleGroup
import com.painzone.domain.exercise.RenameResult
import com.painzone.ui.theme.PainZoneTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryAddEditModal(
    onDismiss: () -> Unit,
    onSubmit: suspend (name: String, muscleGroup: MuscleGroup) -> CreateResult,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        StatefulLibraryAddEditForm(onSubmit = onSubmit, onCancel = onDismiss)
    }
}

@Composable
private fun StatefulLibraryAddEditForm(
    onSubmit: suspend (name: String, muscleGroup: MuscleGroup) -> CreateResult,
    onCancel: () -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var muscleGroup by remember { mutableStateOf<MuscleGroup?>(null) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var saving by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LibraryAddEditForm(
        title = "Nowe ćwiczenie",
        name = name,
        onNameChange = {
            name = it
            if (nameError != null) nameError = null
        },
        muscleGroup = muscleGroup,
        onMuscleGroupChange = { muscleGroup = it },
        muscleGroupEditable = true,
        usage = null,
        nameError = nameError,
        saving = saving,
        onCancel = onCancel,
        onSave = {
            val selected = muscleGroup ?: return@LibraryAddEditForm
            saving = true
            scope.launch {
                when (onSubmit(name, selected)) {
                    CreateResult.DuplicateName -> {
                        nameError = "Ćwiczenie o tej nazwie już istnieje"
                        saving = false
                    }
                    is CreateResult.Success -> {
                        // Parent dismisses the sheet on Success; no local reset needed.
                    }
                }
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LibraryAddEditForm(
    title: String,
    name: String,
    onNameChange: (String) -> Unit,
    muscleGroup: MuscleGroup?,
    onMuscleGroupChange: (MuscleGroup) -> Unit,
    muscleGroupEditable: Boolean,
    usage: ExerciseUsage?,
    nameError: String?,
    saving: Boolean,
    onCancel: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val canSave = name.trim().isNotEmpty() && muscleGroup != null && !saving
    val focusRequester = remember { FocusRequester() }
    var dropdownExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 16.dp)
            .imePadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
        )
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Nazwa") },
            singleLine = true,
            isError = nameError != null,
            supportingText = nameError?.let { { Text(it) } },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
        )
        if (muscleGroupEditable) {
            ExposedDropdownMenuBox(
                expanded = dropdownExpanded,
                onExpandedChange = { dropdownExpanded = it },
            ) {
                OutlinedTextField(
                    value = muscleGroup?.labelPl.orEmpty(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Grupa mięśniowa") },
                    placeholder = { Text("Wybierz grupę…") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded)
                    },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth(),
                )
                ExposedDropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false },
                ) {
                    MuscleGroup.entries.forEach { group ->
                        DropdownMenuItem(
                            text = { Text(group.labelPl) },
                            onClick = {
                                onMuscleGroupChange(group)
                                dropdownExpanded = false
                            },
                        )
                    }
                }
            }
        } else {
            OutlinedTextField(
                value = muscleGroup?.labelPl.orEmpty(),
                onValueChange = {},
                readOnly = true,
                enabled = false,
                label = { Text("Grupa mięśniowa") },
                modifier = Modifier.fillMaxWidth(),
            )
        }
        if (usage != null) {
            Text(
                text = "Używane w ${usage.plansCount} planach · ${usage.sessionsCount} sesjach",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            TextButton(
                onClick = onCancel,
                enabled = !saving,
            ) {
                Text("Anuluj")
            }
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = onSave,
                enabled = canSave,
            ) {
                Text("Zapisz")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryEditExerciseModal(
    target: EditDialogState.Visible,
    onSubmit: suspend (id: Long, newName: String) -> RenameResult,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    // Key form state by exerciseId so switching targets reinitializes name/dirty/errors.
    var name by remember(target.exerciseId) { mutableStateOf(target.initialName) }
    var nameError by remember(target.exerciseId) { mutableStateOf<String?>(null) }
    var saving by remember(target.exerciseId) { mutableStateOf(false) }
    var showDiscardDialog by remember(target.exerciseId) { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Form is dirty when the trimmed name diverges from the initial — confirms before discard.
    val isDirty = name.trim() != target.initialName
    val attemptDismiss = {
        if (isDirty) showDiscardDialog = true else onDismiss()
    }

    ModalBottomSheet(
        onDismissRequest = attemptDismiss,
        sheetState = sheetState,
    ) {
        LibraryAddEditForm(
            title = "Edycja ćwiczenia",
            name = name,
            onNameChange = {
                name = it
                if (nameError != null) nameError = null
            },
            muscleGroup = target.muscleGroup,
            onMuscleGroupChange = {},
            muscleGroupEditable = false,
            usage = target.usage,
            nameError = nameError,
            saving = saving,
            onCancel = attemptDismiss,
            onSave = {
                saving = true
                scope.launch {
                    when (onSubmit(target.exerciseId, name)) {
                        RenameResult.DuplicateName -> {
                            nameError = "Ćwiczenie o tej nazwie już istnieje"
                            saving = false
                        }
                        RenameResult.NotFound -> {
                            saving = false
                            onDismiss()
                        }
                        RenameResult.Success -> {
                            // Parent (VM) hides the dialog state; no local reset needed.
                        }
                    }
                }
            },
        )
    }
    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text("Odrzucić zmiany?") },
            confirmButton = {
                TextButton(onClick = {
                    showDiscardDialog = false
                    onDismiss()
                }) {
                    Text("Odrzuć")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDiscardDialog = false
                    // ModalBottomSheet auto-hides on outside tap before onDismissRequest fires —
                    // re-expand so user actually returns to the editor.
                    scope.launch { sheetState.show() }
                }) {
                    Text("Anuluj")
                }
            },
        )
    }
}

@Preview(showBackground = true, name = "Add — empty")
@Composable
private fun LibraryAddEditFormEmptyPreview() {
    PainZoneTheme {
        Surface {
            LibraryAddEditForm(
                title = "Nowe ćwiczenie",
                name = "",
                onNameChange = {},
                muscleGroup = null,
                onMuscleGroupChange = {},
                muscleGroupEditable = true,
                usage = null,
                nameError = null,
                saving = false,
                onCancel = {},
                onSave = {},
            )
        }
    }
}

@Preview(showBackground = true, name = "Add — filled")
@Composable
private fun LibraryAddEditFormFilledPreview() {
    PainZoneTheme {
        Surface {
            LibraryAddEditForm(
                title = "Nowe ćwiczenie",
                name = "Martwy ciąg",
                onNameChange = {},
                muscleGroup = MuscleGroup.Back,
                onMuscleGroupChange = {},
                muscleGroupEditable = true,
                usage = null,
                nameError = null,
                saving = false,
                onCancel = {},
                onSave = {},
            )
        }
    }
}

@Preview(showBackground = true, name = "Add — duplicate error")
@Composable
private fun LibraryAddEditFormErrorPreview() {
    PainZoneTheme {
        Surface {
            LibraryAddEditForm(
                title = "Nowe ćwiczenie",
                name = "Martwy ciąg",
                onNameChange = {},
                muscleGroup = MuscleGroup.Back,
                onMuscleGroupChange = {},
                muscleGroupEditable = true,
                usage = null,
                nameError = "Ćwiczenie o tej nazwie już istnieje",
                saving = false,
                onCancel = {},
                onSave = {},
            )
        }
    }
}

@Preview(showBackground = true, name = "Edit — pristine")
@Composable
private fun LibraryEditFormPristinePreview() {
    PainZoneTheme {
        Surface {
            LibraryAddEditForm(
                title = "Edycja ćwiczenia",
                name = "Martwy ciąg",
                onNameChange = {},
                muscleGroup = MuscleGroup.Back,
                onMuscleGroupChange = {},
                muscleGroupEditable = false,
                usage = ExerciseUsage(plansCount = 0, sessionsCount = 0),
                nameError = null,
                saving = false,
                onCancel = {},
                onSave = {},
            )
        }
    }
}

@Preview(showBackground = true, name = "Edit — duplicate error")
@Composable
private fun LibraryEditFormErrorPreview() {
    PainZoneTheme {
        Surface {
            LibraryAddEditForm(
                title = "Edycja ćwiczenia",
                name = "Przysiad",
                onNameChange = {},
                muscleGroup = MuscleGroup.Back,
                onMuscleGroupChange = {},
                muscleGroupEditable = false,
                usage = ExerciseUsage(plansCount = 2, sessionsCount = 15),
                nameError = "Ćwiczenie o tej nazwie już istnieje",
                saving = false,
                onCancel = {},
                onSave = {},
            )
        }
    }
}