package com.painzone.ui.plans

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.painzone.domain.plan.PlanSummary
import com.painzone.ui.common.TopLevelTopBar
import com.painzone.ui.theme.PainZoneTheme

@Composable
fun PlansScreen(
    onManageLibrary: () -> Unit,
    onCreatePlan: () -> Unit,
    onOpenPlan: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlansViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val deleteState by viewModel.deleteDialogState.collectAsStateWithLifecycle()
    val confirmState by viewModel.confirmState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.snackbarEvents.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Box(modifier = modifier) {
        PlansScaffold(
            state = state,
            snackbarHostState = snackbarHostState,
            onManageLibrary = onManageLibrary,
            onCreatePlan = onCreatePlan,
            onOpenPlan = onOpenPlan,
            onToggleActive = viewModel::onToggleActive,
            onRequestDelete = viewModel::requestDelete,
        )
        (deleteState as? DeletePlanDialogState.Visible)?.let { visible ->
            DeletePlanDialog(
                planName = visible.planName,
                onConfirm = viewModel::confirmDelete,
                onDismiss = viewModel::cancelDelete,
            )
        }
        (confirmState as? ActivationConfirmState.Visible)?.let { visible ->
            ActivationConfirmDialog(
                state = visible,
                onConfirm = viewModel::confirmActivation,
                onDismiss = viewModel::cancelActivation,
            )
        }
    }
}

@Composable
private fun PlansScaffold(
    state: PlansUiState,
    snackbarHostState: SnackbarHostState,
    onManageLibrary: () -> Unit,
    onCreatePlan: () -> Unit,
    onOpenPlan: (Long) -> Unit,
    onToggleActive: (PlanSummary) -> Unit,
    onRequestDelete: (Long, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = { TopLevelTopBar(title = "Plany", onManageLibrary = onManageLibrary) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onCreatePlan,
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Nowy plan") },
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        when (state) {
            PlansUiState.Loading -> LoadingBody(innerPadding)
            PlansUiState.Empty -> EmptyBody(innerPadding)
            is PlansUiState.Content -> ContentBody(
                items = state.items,
                innerPadding = innerPadding,
                onOpenPlan = onOpenPlan,
                onToggleActive = onToggleActive,
                onRequestDelete = onRequestDelete,
            )
        }
    }
}

@Composable
private fun LoadingBody(innerPadding: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyBody(innerPadding: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "Brak planów",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "Stwórz pierwszy plan przyciskiem „+ Nowy plan” poniżej.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun ContentBody(
    items: List<PlanSummary>,
    innerPadding: PaddingValues,
    onOpenPlan: (Long) -> Unit,
    onToggleActive: (PlanSummary) -> Unit,
    onRequestDelete: (Long, String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
    ) {
        items(items = items, key = { it.id }) { plan ->
            ListItem(
                headlineContent = { Text(plan.name) },
                supportingContent = { Text(dayCountLabel(plan.dayCount)) },
                trailingContent = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(onClick = { onToggleActive(plan) }) {
                            Icon(
                                imageVector = if (plan.isActive) {
                                    Icons.Filled.Star
                                } else {
                                    Icons.Outlined.StarBorder
                                },
                                contentDescription = if (plan.isActive) {
                                    "Odznacz plan ${plan.name}"
                                } else {
                                    "Aktywuj plan ${plan.name}"
                                },
                                tint = if (plan.isActive) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                            )
                        }
                        IconButton(onClick = { onRequestDelete(plan.id, plan.name) }) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Usuń ${plan.name}",
                            )
                        }
                    }
                },
                modifier = Modifier.clickable { onOpenPlan(plan.id) },
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        }
    }
}

@Composable
private fun DeletePlanDialog(
    planName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Usunąć plan?") },
        text = { Text("Plan „$planName” wraz ze wszystkimi ćwiczeniami zostanie usunięty.") },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Usuń") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Anuluj") }
        },
    )
}

// Polish declension for "N training days per week":
// 1 → "dzień treningowy", 2-4 → "dni treningowe", 5+ → "dni treningowych".
private fun dayCountLabel(count: Int): String {
    val mod10 = count % 10
    val mod100 = count % 100
    val noun = when {
        count == 1 -> "dzień treningowy"
        mod10 in 2..4 && mod100 !in 12..14 -> "dni treningowe"
        else -> "dni treningowych"
    }
    return "$count $noun w skali tygodnia"
}

private val previewPlans = listOf(
    PlanSummary(id = 1L, name = "Push/Pull/Legs", isActive = true, dayCount = 3),
    PlanSummary(id = 2L, name = "Full Body Beginner", isActive = false, dayCount = 2),
    PlanSummary(id = 3L, name = "Upper/Lower", isActive = false, dayCount = 1),
)

@Preview(showBackground = true, name = "Loading")
@Composable
private fun PlansScreenLoadingPreview() {
    PainZoneTheme {
        Surface {
            PlansScaffold(
                state = PlansUiState.Loading,
                snackbarHostState = remember { SnackbarHostState() },
                onManageLibrary = {},
                onCreatePlan = {},
                onOpenPlan = {},
                onToggleActive = {},
                onRequestDelete = { _, _ -> },
            )
        }
    }
}

@Preview(showBackground = true, name = "Empty")
@Composable
private fun PlansScreenEmptyPreview() {
    PainZoneTheme {
        Surface {
            PlansScaffold(
                state = PlansUiState.Empty,
                snackbarHostState = remember { SnackbarHostState() },
                onManageLibrary = {},
                onCreatePlan = {},
                onOpenPlan = {},
                onToggleActive = {},
                onRequestDelete = { _, _ -> },
            )
        }
    }
}

@Preview(showBackground = true, name = "Content")
@Composable
private fun PlansScreenContentPreview() {
    PainZoneTheme {
        Surface {
            PlansScaffold(
                state = PlansUiState.Content(previewPlans),
                snackbarHostState = remember { SnackbarHostState() },
                onManageLibrary = {},
                onCreatePlan = {},
                onOpenPlan = {},
                onToggleActive = {},
                onRequestDelete = { _, _ -> },
            )
        }
    }
}

@Preview(showBackground = true, name = "Content (1 item)")
@Composable
private fun PlansScreenContentSinglePreview() {
    PainZoneTheme {
        Surface {
            PlansScaffold(
                state = PlansUiState.Content(previewPlans.take(1)),
                snackbarHostState = remember { SnackbarHostState() },
                onManageLibrary = {},
                onCreatePlan = {},
                onOpenPlan = {},
                onToggleActive = {},
                onRequestDelete = { _, _ -> },
            )
        }
    }
}