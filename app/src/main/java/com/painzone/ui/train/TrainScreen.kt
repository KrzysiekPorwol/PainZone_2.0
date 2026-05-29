package com.painzone.ui.train

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.painzone.ui.common.TopLevelTopBar
import com.painzone.ui.theme.PainZoneTheme

@Composable
fun TrainScreen(
    onManageLibrary: () -> Unit,
    onGoToPlans: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TrainViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    TrainScaffold(
        state = state,
        onManageLibrary = onManageLibrary,
        onGoToPlans = onGoToPlans,
        modifier = modifier,
    )
}

@Composable
private fun TrainScaffold(
    state: TrainUiState,
    onManageLibrary: () -> Unit,
    onGoToPlans: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = { TopLevelTopBar(title = "Trenuj", onManageLibrary = onManageLibrary) },
    ) { innerPadding ->
        when (state) {
            TrainUiState.Loading -> LoadingBody(innerPadding)
            TrainUiState.NoActivePlan -> NoActivePlanBody(innerPadding, onGoToPlans)
            is TrainUiState.ActivePlan -> ActivePlanBody(state.planName, innerPadding)
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
private fun ActivePlanBody(planName: String, innerPadding: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp),
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "Aktywny plan",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(text = planName, style = MaterialTheme.typography.titleLarge)
                // "Zacznij" is a placeholder until sessions land in M3.
                Button(onClick = {}, enabled = false, modifier = Modifier.fillMaxWidth()) {
                    Text("Zacznij")
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Loading")
@Composable
private fun TrainLoadingPreview() {
    PainZoneTheme { Surface { TrainScaffold(TrainUiState.Loading, {}, {}) } }
}

@Preview(showBackground = true, name = "No active plan")
@Composable
private fun TrainNoActivePlanPreview() {
    PainZoneTheme { Surface { TrainScaffold(TrainUiState.NoActivePlan, {}, {}) } }
}

@Preview(showBackground = true, name = "Active plan")
@Composable
private fun TrainActivePlanPreview() {
    PainZoneTheme { Surface { TrainScaffold(TrainUiState.ActivePlan("Push/Pull/Legs"), {}, {}) } }
}