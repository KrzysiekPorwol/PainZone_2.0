package com.painzone.ui.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
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
import com.painzone.domain.stats.StatsPeriod
import com.painzone.ui.theme.PainZoneTheme

@Composable
fun StatsExerciseScreen(
    exerciseName: String,
    muscleGroupLabel: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StatsExerciseViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val period by viewModel.selectedPeriod.collectAsStateWithLifecycle()
    val isDeleted by viewModel.isDeleted.collectAsStateWithLifecycle()
    StatsExerciseScaffold(
        exerciseName = exerciseName,
        muscleGroupLabel = muscleGroupLabel,
        state = state,
        selectedPeriod = period,
        isDeleted = isDeleted,
        onBack = onBack,
        onSelectPeriod = viewModel::selectPeriod,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatsExerciseScaffold(
    exerciseName: String,
    muscleGroupLabel: String,
    state: StatsUiState,
    selectedPeriod: StatsPeriod,
    isDeleted: Boolean,
    onBack: () -> Unit,
    onSelectPeriod: (StatsPeriod) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(exerciseName)
                        Text(
                            text = muscleGroupLabel,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            if (isDeleted) {
                DeletedBanner(modifier = Modifier.fillMaxWidth())
            }
            PeriodFilter(
                selected = selectedPeriod,
                onSelect = onSelectPeriod,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            )
            when (state) {
                StatsUiState.Loading -> LoadingBody()
                StatsUiState.Empty -> EmptyBody()
                is StatsUiState.Content -> {
                    BestSetCard(
                        best = state.best,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                    )
                    SessionList(state.sessions)
                }
            }
        }
    }
}

private val periodLabels: List<Pair<StatsPeriod, String>> = listOf(
    StatsPeriod.LAST_30_DAYS to "30 dni",
    StatsPeriod.LAST_90_DAYS to "90 dni",
    StatsPeriod.LAST_YEAR to "Rok",
    StatsPeriod.ALL to "Wszystko",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PeriodFilter(
    selected: StatsPeriod,
    onSelect: (StatsPeriod) -> Unit,
    modifier: Modifier = Modifier,
) {
    SingleChoiceSegmentedButtonRow(modifier = modifier) {
        periodLabels.forEachIndexed { index, (period, label) ->
            SegmentedButton(
                selected = period == selected,
                onClick = { onSelect(period) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = periodLabels.size),
                icon = {},
            ) {
                Text(label, maxLines = 1)
            }
        }
    }
}

@Composable
private fun BestSetCard(best: BestSetUi, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Text(
            text = best.text,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(16.dp),
        )
    }
}

// Soft-deleted exercise (M4.5): history is read-only and the exercise no longer exists in the
// library. The frozen name/group in the TopBar still identify it; this banner flags the state.
@Composable
private fun DeletedBanner(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
    ) {
        Text(
            text = "Ćwiczenie usunięte — read-only",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        )
    }
}

@Composable
private fun LoadingBody() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyBody() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = "Brak serii w tym okresie",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "Zmień filtr powyżej.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun SessionList(sessions: List<StatsSessionUi>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp),
    ) {
        sessions.forEach { session ->
            item(key = "h-${session.sessionId}") {
                Text(
                    text = session.header,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 4.dp),
                )
            }
            items(items = session.sets, key = { it.setId }) { set ->
                Text(
                    text = set.text,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                )
            }
        }
    }
}

// --- Previews ---

private val previewSessions = listOf(
    StatsSessionUi(
        sessionId = 2L,
        header = "22.06 · Push/Pull/Legs · Push A",
        sets = listOf(
            StatsSetUi(20L, "8 × 80 kg · RPE Normalna · —"),
            StatsSetUi(21L, "8 × 80 kg · RPE Ciężka · po 120s odpocz."),
            StatsSetUi(22L, "6 × 82.5 kg · RPE Ciężka · po 150s odpocz."),
        ),
    ),
    StatsSessionUi(
        sessionId = 1L,
        header = "15.06 · Push/Pull/Legs · Push A",
        sets = listOf(
            StatsSetUi(10L, "8 × 77.5 kg · RPE Normalna · —"),
            StatsSetUi(11L, "8 × 77.5 kg · po 120s odpocz."),
        ),
    ),
)

@Preview(showBackground = true, name = "Loading")
@Composable
private fun StatsExerciseLoadingPreview() {
    PainZoneTheme {
        Surface {
            StatsExerciseScaffold(
                exerciseName = "Wyciskanie sztangi",
                muscleGroupLabel = "Klatka",
                state = StatsUiState.Loading,
                selectedPeriod = StatsPeriod.LAST_90_DAYS,
                isDeleted = false,
                onBack = {},
                onSelectPeriod = {},
            )
        }
    }
}

@Preview(showBackground = true, name = "Empty")
@Composable
private fun StatsExerciseEmptyPreview() {
    PainZoneTheme {
        Surface {
            StatsExerciseScaffold(
                exerciseName = "Wyciskanie sztangi",
                muscleGroupLabel = "Klatka",
                state = StatsUiState.Empty,
                selectedPeriod = StatsPeriod.LAST_30_DAYS,
                isDeleted = false,
                onBack = {},
                onSelectPeriod = {},
            )
        }
    }
}

@Preview(showBackground = true, name = "Content")
@Composable
private fun StatsExerciseContentPreview() {
    PainZoneTheme {
        Surface {
            StatsExerciseScaffold(
                exerciseName = "Wyciskanie sztangi",
                muscleGroupLabel = "Klatka",
                state = StatsUiState.Content(
                    best = BestSetUi("Best: 6 × 82.5 kg · 1RM≈99 kg · dziś"),
                    sessions = previewSessions,
                ),
                selectedPeriod = StatsPeriod.LAST_90_DAYS,
                isDeleted = false,
                onBack = {},
                onSelectPeriod = {},
            )
        }
    }
}

@Preview(showBackground = true, name = "Deleted")
@Composable
private fun StatsExerciseDeletedPreview() {
    PainZoneTheme {
        Surface {
            StatsExerciseScaffold(
                exerciseName = "Wyciskanie sztangi",
                muscleGroupLabel = "Klatka",
                state = StatsUiState.Content(
                    best = BestSetUi("Best: 6 × 82.5 kg · 1RM≈99 kg · 12 dni temu"),
                    sessions = previewSessions,
                ),
                selectedPeriod = StatsPeriod.LAST_90_DAYS,
                isDeleted = true,
                onBack = {},
                onSelectPeriod = {},
            )
        }
    }
}
