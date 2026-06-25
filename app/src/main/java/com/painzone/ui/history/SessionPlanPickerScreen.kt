package com.painzone.ui.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.painzone.ui.theme.PainZoneTheme

@Composable
fun SessionPlanPickerScreen(
    onBack: () -> Unit,
    onSelectPlan: (planName: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SessionPlanPickerViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    SessionPlanPickerScaffold(
        state = state,
        onBack = onBack,
        onSelectPlan = onSelectPlan,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SessionPlanPickerScaffold(
    state: SessionPlanPickerUiState,
    onBack: () -> Unit,
    onSelectPlan: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Po planie") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Wstecz",
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        when (state) {
            SessionPlanPickerUiState.Loading -> CenterBox(innerPadding) { CircularProgressIndicator() }
            SessionPlanPickerUiState.Empty -> EmptyBody(innerPadding)
            is SessionPlanPickerUiState.Content -> PlanList(state.planNames, innerPadding, onSelectPlan)
        }
    }
}

@Composable
private fun CenterBox(innerPadding: PaddingValues, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentAlignment = Alignment.Center,
    ) { content() }
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
        Text(
            text = "Żaden plan nie ma jeszcze sesji",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun PlanList(
    planNames: List<String>,
    innerPadding: PaddingValues,
    onSelectPlan: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
    ) {
        items(items = planNames, key = { it }) { name ->
            ListItem(
                headlineContent = { Text(name) },
                modifier = Modifier.clickable { onSelectPlan(name) },
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        }
    }
}

@Preview(showBackground = true, name = "Loading")
@Composable
private fun SessionPlanPickerLoadingPreview() {
    PainZoneTheme {
        Surface { SessionPlanPickerScaffold(SessionPlanPickerUiState.Loading, {}, {}) }
    }
}

@Preview(showBackground = true, name = "Empty")
@Composable
private fun SessionPlanPickerEmptyPreview() {
    PainZoneTheme {
        Surface { SessionPlanPickerScaffold(SessionPlanPickerUiState.Empty, {}, {}) }
    }
}

@Preview(showBackground = true, name = "Content")
@Composable
private fun SessionPlanPickerContentPreview() {
    PainZoneTheme {
        Surface {
            SessionPlanPickerScaffold(
                SessionPlanPickerUiState.Content(listOf("Push/Pull/Legs", "Full Body")),
                {},
                {},
            )
        }
    }
}
