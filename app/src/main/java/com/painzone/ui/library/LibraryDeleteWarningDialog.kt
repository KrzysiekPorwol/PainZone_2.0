package com.painzone.ui.library

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.painzone.domain.exercise.ExerciseUsage
import com.painzone.ui.theme.PainZoneTheme

@Composable
fun LibraryDeleteWarningDialog(
    exerciseName: String,
    usage: ExerciseUsage,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Usunąć „$exerciseName”?") },
        text = {
            Text(
                text = "Używane w ${usage.plansCount} planach · " +
                    "${usage.sessionsCount} sesjach — historia zostanie zachowana jako read-only.",
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = "Usuń",
                    color = MaterialTheme.colorScheme.error,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Anuluj")
            }
        },
    )
}

@Preview(showBackground = true, name = "Dialog (usage 0/0)")
@Composable
private fun LibraryDeleteWarningDialogEmptyUsagePreview() {
    PainZoneTheme {
        Surface {
            LibraryDeleteWarningDialog(
                exerciseName = "Martwy ciąg",
                usage = ExerciseUsage(plansCount = 0, sessionsCount = 0),
                onConfirm = {},
                onDismiss = {},
            )
        }
    }
}

@Preview(showBackground = true, name = "Dialog (usage 2/15)")
@Composable
private fun LibraryDeleteWarningDialogWithUsagePreview() {
    PainZoneTheme {
        Surface {
            LibraryDeleteWarningDialog(
                exerciseName = "Przysiad ze sztangą",
                usage = ExerciseUsage(plansCount = 2, sessionsCount = 15),
                onConfirm = {},
                onDismiss = {},
            )
        }
    }
}
