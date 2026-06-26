package com.painzone.ui.library

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.painzone.ui.theme.PainZoneTheme

/**
 * Shown instead of the delete confirm when the exercise is still referenced by ≥1 plan.
 * The user must first remove it from those plans, then delete it from the library.
 */
@Composable
fun LibraryDeleteBlockedDialog(
    exerciseName: String,
    planNames: List<String>,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nie można usunąć „$exerciseName”") },
        text = {
            Column {
                Text("Ćwiczenie jest używane w planach:")
                planNames.forEach { name ->
                    Text(
                        text = "• $name",
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
                Text(
                    text = "Usuń je z tych planów, aby móc usunąć z biblioteki.",
                    modifier = Modifier.padding(top = 12.dp),
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Rozumiem")
            }
        },
    )
}

@Preview(showBackground = true, name = "Blocked (1 plan)")
@Composable
private fun LibraryDeleteBlockedDialogSinglePreview() {
    PainZoneTheme {
        Surface {
            LibraryDeleteBlockedDialog(
                exerciseName = "Wyciskanie sztangi",
                planNames = listOf("Push"),
                onDismiss = {},
            )
        }
    }
}

@Preview(showBackground = true, name = "Blocked (kilka planów)")
@Composable
private fun LibraryDeleteBlockedDialogMultiPreview() {
    PainZoneTheme {
        Surface {
            LibraryDeleteBlockedDialog(
                exerciseName = "Przysiad ze sztangą",
                planNames = listOf("Legs", "Push/Pull/Legs", "FBW"),
                onDismiss = {},
            )
        }
    }
}
