package com.painzone.ui.train

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.painzone.ui.common.TopLevelTopBar
import com.painzone.ui.plans.toImageVector
import com.painzone.ui.theme.PainZoneTheme

@Composable
fun TrainScreen(
    onManageLibrary: () -> Unit,
    onGoToPlans: () -> Unit,
    onOpenSession: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TrainViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.openSession.collect { onOpenSession(it) }
    }
    LaunchedEffect(Unit) {
        viewModel.snackbarEvents.collect { snackbarHostState.showSnackbar(it) }
    }

    TrainScaffold(
        state = state,
        snackbarHostState = snackbarHostState,
        onManageLibrary = onManageLibrary,
        onGoToPlans = onGoToPlans,
        onStartDay = viewModel::onStartDay,
        onResume = onOpenSession,
        modifier = modifier,
    )
}

@Composable
private fun TrainScaffold(
    state: TrainUiState,
    snackbarHostState: SnackbarHostState,
    onManageLibrary: () -> Unit,
    onGoToPlans: () -> Unit,
    onStartDay: (Long) -> Unit,
    onResume: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = { TopLevelTopBar(title = "Trenuj", onManageLibrary = onManageLibrary) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        when (state) {
            TrainUiState.Loading -> LoadingBody(innerPadding)
            is TrainUiState.Loaded ->
                if (state.isEmpty) {
                    NoActivePlanBody(innerPadding, onGoToPlans)
                } else {
                    LoadedBody(state, innerPadding, onStartDay, onResume)
                }
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
private fun NoActivePlanBody(innerPadding: PaddingValues, onGoToPlans: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Brak aktywnego planu",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "Aktywuj plan, aby zacząć trening.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            Button(onClick = onGoToPlans) { Text("Przejdź do planów") }
        }
    }
}

@Composable
private fun LoadedBody(
    state: TrainUiState.Loaded,
    innerPadding: PaddingValues,
    onStartDay: (Long) -> Unit,
    onResume: (Long) -> Unit,
) {
    // Can't start a second session while one is in progress (≤1 global).
    val sessionInProgress = state.resume != null
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        state.resume?.let { ResumeCard(it, onResume) }
        state.activePlan?.let { plan ->
            ActivePlanCard(plan, sessionInProgress = sessionInProgress, onStartDay = onStartDay)
        }
    }
}

@Composable
private fun ResumeCard(resume: ResumeInfo, onResume: (Long) -> Unit) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            CardHeader(
                icon = Icons.Filled.PlayArrow,
                eyebrow = "Sesja w toku",
                title = "${resume.planName} · ${resume.dayName}",
            )
            Button(
                onClick = { onResume(resume.sessionId) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Wznów sesję")
            }
        }
    }
}

@Composable
private fun ActivePlanCard(
    plan: ActivePlanInfo,
    sessionInProgress: Boolean,
    onStartDay: (Long) -> Unit,
) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            CardHeader(
                icon = plan.planIcon.toImageVector(),
                eyebrow = "Aktywny plan",
                title = plan.planName,
            )
            val hint = when {
                sessionInProgress -> "Zakończ bieżącą sesję, aby zacząć nową."
                plan.allDays.isEmpty() -> "Dodaj sesję treningową do planu, aby zacząć."
                else -> "Wybierz sesję do rozpoczęcia."
            }
            Text(
                text = hint,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            // One start button per day. The suggested day (rotation) is filled with the primary
            // accent + "· sugerowane"; the rest are outlined so any day is one tap away.
            plan.allDays.forEach { day ->
                val isSuggested = day.dayId == plan.suggestedDay?.dayId
                val label = if (isSuggested) {
                    "Zacznij: ${day.dayName} · sugerowane"
                } else {
                    "Zacznij: ${day.dayName}"
                }
                if (isSuggested) {
                    Button(
                        onClick = { onStartDay(day.dayId) },
                        enabled = !sessionInProgress,
                        modifier = Modifier.fillMaxWidth(),
                    ) { Text(label) }
                } else {
                    OutlinedButton(
                        onClick = { onStartDay(day.dayId) },
                        enabled = !sessionInProgress,
                        modifier = Modifier.fillMaxWidth(),
                    ) { Text(label) }
                }
            }
        }
    }
}

// Leading-icon header shared by the Train cards, matching the Postęp hub card look.
@Composable
private fun CardHeader(icon: ImageVector, eyebrow: String, title: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = eyebrow,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(text = title, style = MaterialTheme.typography.titleMedium)
        }
    }
}

private val previewPlan = ActivePlanInfo(
    planName = "Push/Pull/Legs",
    suggestedDay = StartableDay(2L, "Pull"),
    allDays = listOf(
        StartableDay(1L, "Push"),
        StartableDay(2L, "Pull"),
        StartableDay(3L, "Legs"),
    ),
)

@Preview(showBackground = true, name = "Loading")
@Composable
private fun TrainLoadingPreview() {
    PainZoneTheme {
        Surface {
            TrainScaffold(TrainUiState.Loading, remember { SnackbarHostState() }, {}, {}, {}, {})
        }
    }
}

@Preview(showBackground = true, name = "Empty (no plan, no session)")
@Composable
private fun TrainEmptyPreview() {
    PainZoneTheme {
        Surface {
            TrainScaffold(
                TrainUiState.Loaded(resume = null, activePlan = null),
                remember { SnackbarHostState() }, {}, {}, {}, {},
            )
        }
    }
}

@Preview(showBackground = true, name = "Active plan — start")
@Composable
private fun TrainActivePlanPreview() {
    PainZoneTheme {
        Surface {
            TrainScaffold(
                TrainUiState.Loaded(resume = null, activePlan = previewPlan),
                remember { SnackbarHostState() }, {}, {}, {}, {},
            )
        }
    }
}

@Preview(showBackground = true, name = "Resume + active plan")
@Composable
private fun TrainResumePreview() {
    PainZoneTheme {
        Surface {
            TrainScaffold(
                TrainUiState.Loaded(
                    resume = ResumeInfo(42L, "Full Body A", "Dzień 1"),
                    activePlan = previewPlan,
                ),
                remember { SnackbarHostState() }, {}, {}, {}, {},
            )
        }
    }
}
