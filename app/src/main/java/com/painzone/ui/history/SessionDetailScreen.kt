package com.painzone.ui.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
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
fun SessionDetailScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SessionDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    SessionDetailScaffold(state = state, onBack = onBack, modifier = modifier)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SessionDetailScaffold(
    state: SessionDetailUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Szczegóły sesji") },
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
            SessionDetailUiState.Loading -> CenterBox(innerPadding) { CircularProgressIndicator() }
            SessionDetailUiState.NotFound -> CenterBox(innerPadding) {
                Text(
                    text = "Nie znaleziono sesji",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
            is SessionDetailUiState.Content -> ContentBody(state, innerPadding)
        }
    }
}

@Composable
private fun ContentBody(state: SessionDetailUiState.Content, innerPadding: PaddingValues) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(key = "header") {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = state.title, style = MaterialTheme.typography.titleLarge)
                Text(
                    text = state.stats,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        items(items = state.exercises, key = { it.snapshotId }) { exercise ->
            ExerciseCard(exercise)
        }
    }
}

@Composable
private fun ExerciseCard(exercise: SessionExerciseDetailUi) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f, fill = false),
                )
                if (exercise.isDeleted) DeletedMarker()
            }
            if (exercise.sets.isEmpty()) {
                Text(
                    text = "Brak serii",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                exercise.sets.forEachIndexed { index, line ->
                    SetRow(order = index + 1, line = line)
                }
            }
        }
    }
}

@Composable
private fun SetRow(order: Int, line: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "Seria $order",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(text = line, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun DeletedMarker() {
    Surface(
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        shape = MaterialTheme.shapes.small,
    ) {
        Text(
            text = "usunięte",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
        )
    }
}

@Composable
private fun CenterBox(innerPadding: PaddingValues, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(32.dp),
        contentAlignment = Alignment.Center,
    ) { content() }
}

private val previewContent = SessionDetailUiState.Content(
    title = "24.06 · Push/Pull/Legs · Push A",
    stats = "czas 1 godz 5 min · tonaż 4250 kg",
    exercises = listOf(
        SessionExerciseDetailUi(
            snapshotId = 1L,
            name = "Wyciskanie sztangi",
            isDeleted = false,
            sets = listOf(
                "10 × 60 kg · RPE Normalna · —",
                "8 × 65 kg · RPE Ciężka · po 90s odpocz.",
                "6 × 70 kg · RPE Ciężka · po 120s odpocz.",
            ),
        ),
        SessionExerciseDetailUi(
            snapshotId = 2L,
            name = "Rozpiętki (stare)",
            isDeleted = true,
            sets = listOf(
                "12 × 12.5 kg · —",
                "12 × 12.5 kg · po 60s odpocz.",
            ),
        ),
    ),
)

@Preview(showBackground = true, name = "Loading")
@Composable
private fun SessionDetailLoadingPreview() {
    PainZoneTheme { Surface { SessionDetailScaffold(SessionDetailUiState.Loading, {}) } }
}

@Preview(showBackground = true, name = "NotFound")
@Composable
private fun SessionDetailNotFoundPreview() {
    PainZoneTheme { Surface { SessionDetailScaffold(SessionDetailUiState.NotFound, {}) } }
}

@Preview(showBackground = true, name = "Content")
@Composable
private fun SessionDetailContentPreview() {
    PainZoneTheme { Surface { SessionDetailScaffold(previewContent, {}) } }
}
