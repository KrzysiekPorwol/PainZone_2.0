package com.painzone.ui.plans

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

// Shared between PlansScreen (S2) and PlanDetailScreen (S4): both expose the ⭐ toggle,
// so the confirm dialog, its state, and the "should we ask?" decision live in one place.

sealed interface ActivationConfirmState {
    data object Hidden : ActivationConfirmState
    // Activating planId would replace currentActiveName — wireframe S4 "confirm-gdy-zmiana".
    data class Visible(
        val planId: Long,
        val planName: String,
        val currentActiveName: String,
    ) : ActivationConfirmState
}

sealed interface ActivationDecision {
    data object Activate : ActivationDecision
    data object Deactivate : ActivationDecision
    // A different plan is active and would be replaced — confirm first.
    data class Confirm(val currentActiveName: String) : ActivationDecision
}

// Pure: tap ⭐ on a plan that is [targetIsActive], while [otherActiveName] is the name of a
// *different* active plan (null if none). Deactivation and first activation are silent.
fun activationDecision(targetIsActive: Boolean, otherActiveName: String?): ActivationDecision =
    when {
        targetIsActive -> ActivationDecision.Deactivate
        otherActiveName != null -> ActivationDecision.Confirm(otherActiveName)
        else -> ActivationDecision.Activate
    }

@Composable
fun ActivationConfirmDialog(
    state: ActivationConfirmState.Visible,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Aktywować plan?") },
        text = {
            Text(
                "Plan „${state.planName}” stanie się aktywny i zastąpi „${state.currentActiveName}”.",
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Aktywuj") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Anuluj") }
        },
    )
}