package com.painzone.ui.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.painzone.domain.exercise.MuscleGroup
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
        name = name,
        onNameChange = {
            name = it
            if (nameError != null) nameError = null
        },
        muscleGroup = muscleGroup,
        onMuscleGroupChange = { muscleGroup = it },
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
    name: String,
    onNameChange: (String) -> Unit,
    muscleGroup: MuscleGroup?,
    onMuscleGroupChange: (MuscleGroup) -> Unit,
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
            text = "Nowe ćwiczenie",
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

@Preview(showBackground = true, name = "Form — empty")
@Composable
private fun LibraryAddEditFormEmptyPreview() {
    PainZoneTheme {
        Surface {
            LibraryAddEditForm(
                name = "",
                onNameChange = {},
                muscleGroup = null,
                onMuscleGroupChange = {},
                nameError = null,
                saving = false,
                onCancel = {},
                onSave = {},
            )
        }
    }
}

@Preview(showBackground = true, name = "Form — filled")
@Composable
private fun LibraryAddEditFormFilledPreview() {
    PainZoneTheme {
        Surface {
            LibraryAddEditForm(
                name = "Martwy ciąg",
                onNameChange = {},
                muscleGroup = MuscleGroup.Back,
                onMuscleGroupChange = {},
                nameError = null,
                saving = false,
                onCancel = {},
                onSave = {},
            )
        }
    }
}

@Preview(showBackground = true, name = "Form — duplicate error")
@Composable
private fun LibraryAddEditFormErrorPreview() {
    PainZoneTheme {
        Surface {
            LibraryAddEditForm(
                name = "Martwy ciąg",
                onNameChange = {},
                muscleGroup = MuscleGroup.Back,
                onMuscleGroupChange = {},
                nameError = "Ćwiczenie o tej nazwie już istnieje",
                saving = false,
                onCancel = {},
                onSave = {},
            )
        }
    }
}