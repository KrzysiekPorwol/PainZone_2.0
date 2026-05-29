package com.painzone.ui.plans.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import com.painzone.domain.plan.PlannedDay
import com.painzone.ui.theme.PainZoneTheme

@Composable
fun PlanDetailScreen(
    onBack: () -> Unit,
    onOpenDay: (dayId: Long, dayName: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlanDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    PlanDetailScaffold(
        state = state,
        onBack = onBack,
        onOpenDay = onOpenDay,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlanDetailScaffold(
    state: PlanDetailUiState,
    onBack: () -> Unit,
    onOpenDay: (dayId: Long, dayName: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val title = (state as? PlanDetailUiState.Content)?.planName ?: "Plan"
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Wróć",
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        when (state) {
            PlanDetailUiState.Loading -> CenteredBody(innerPadding) { CircularProgressIndicator() }
            PlanDetailUiState.NotFound -> CenteredBody(innerPadding) {
                Text(
                    text = "Plan nie istnieje.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            is PlanDetailUiState.Content ->
                if (state.days.isEmpty()) {
                    EmptyBody(innerPadding)
                } else {
                    DaysList(days = state.days, innerPadding = innerPadding, onOpenDay = onOpenDay)
                }
        }
    }
}

@Composable
private fun CenteredBody(innerPadding: PaddingValues, content: @Composable () -> Unit) {
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "Brak sesji w tym planie",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "Sesje dodasz przy tworzeniu planu.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun DaysList(
    days: List<PlannedDay>,
    innerPadding: PaddingValues,
    onOpenDay: (dayId: Long, dayName: String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
    ) {
        items(items = days, key = { it.id }) { day ->
            ListItem(
                headlineContent = { Text(day.name) },
                trailingContent = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                    )
                },
                modifier = Modifier.clickable { onOpenDay(day.id, day.name) },
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        }
    }
}

private val previewDays = listOf(
    PlannedDay(id = 1L, trainingPlanId = 1L, name = "Push", order = 0),
    PlannedDay(id = 2L, trainingPlanId = 1L, name = "Pull", order = 1),
    PlannedDay(id = 3L, trainingPlanId = 1L, name = "Legs", order = 2),
)

@Preview(showBackground = true, name = "Loading")
@Composable
private fun PlanDetailLoadingPreview() {
    PainZoneTheme {
        Surface { PlanDetailScaffold(PlanDetailUiState.Loading, {}, { _, _ -> }) }
    }
}

@Preview(showBackground = true, name = "Not found")
@Composable
private fun PlanDetailNotFoundPreview() {
    PainZoneTheme {
        Surface { PlanDetailScaffold(PlanDetailUiState.NotFound, {}, { _, _ -> }) }
    }
}

@Preview(showBackground = true, name = "Empty")
@Composable
private fun PlanDetailEmptyPreview() {
    PainZoneTheme {
        Surface {
            PlanDetailScaffold(
                PlanDetailUiState.Content(planName = "Nowy plan", days = emptyList()),
                {}, { _, _ -> },
            )
        }
    }
}

@Preview(showBackground = true, name = "Content")
@Composable
private fun PlanDetailContentPreview() {
    PainZoneTheme {
        Surface {
            PlanDetailScaffold(
                PlanDetailUiState.Content(planName = "Push/Pull/Legs", days = previewDays),
                {}, { _, _ -> },
            )
        }
    }
}