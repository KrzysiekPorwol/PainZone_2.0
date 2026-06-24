package com.painzone.ui.progress

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
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
import com.painzone.domain.exercise.Exercise
import com.painzone.domain.exercise.MuscleGroup
import com.painzone.ui.common.TopLevelTopBar
import com.painzone.ui.library.labelPl
import com.painzone.ui.theme.PainZoneTheme
import java.time.Instant

@Composable
fun ProgressScreen(
    onManageLibrary: () -> Unit,
    onOpenStats: (exerciseId: Long, exerciseName: String, muscleGroupLabel: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProgressViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ProgressScaffold(
        state = state,
        onManageLibrary = onManageLibrary,
        onOpenStats = onOpenStats,
        modifier = modifier,
    )
}

@Composable
private fun ProgressScaffold(
    state: ProgressUiState,
    onManageLibrary: () -> Unit,
    onOpenStats: (Long, String, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = { TopLevelTopBar(title = "Postęp", onManageLibrary = onManageLibrary) },
    ) { innerPadding ->
        when (state) {
            ProgressUiState.Loading -> CenterBox(innerPadding) { CircularProgressIndicator() }
            ProgressUiState.Empty -> EmptyBody(innerPadding)
            is ProgressUiState.Content -> ExerciseList(state.exercises, innerPadding, onOpenStats)
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "Brak historii",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "Zakończ pierwszą sesję, aby zobaczyć postęp.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun ExerciseList(
    items: List<Exercise>,
    innerPadding: PaddingValues,
    onOpenStats: (Long, String, String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
    ) {
        items(items = items, key = { it.id }) { exercise ->
            val label = exercise.muscleGroup.labelPl
            ListItem(
                headlineContent = { Text(exercise.name) },
                supportingContent = { Text(label) },
                modifier = Modifier.clickable { onOpenStats(exercise.id, exercise.name, label) },
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        }
    }
}

private val previewNow: Instant = Instant.parse("2026-06-24T10:00:00Z")

private val previewExercises = listOf(
    Exercise(1L, "Wyciskanie sztangi", MuscleGroup.Chest, previewNow, null),
    Exercise(2L, "Martwy ciąg", MuscleGroup.Back, previewNow, null),
    Exercise(3L, "Przysiad ze sztangą", MuscleGroup.Legs, previewNow, null),
)

@Preview(showBackground = true, name = "Loading")
@Composable
private fun ProgressLoadingPreview() {
    PainZoneTheme {
        Surface { ProgressScaffold(ProgressUiState.Loading, {}, { _, _, _ -> }) }
    }
}

@Preview(showBackground = true, name = "Empty")
@Composable
private fun ProgressEmptyPreview() {
    PainZoneTheme {
        Surface { ProgressScaffold(ProgressUiState.Empty, {}, { _, _, _ -> }) }
    }
}

@Preview(showBackground = true, name = "Content")
@Composable
private fun ProgressContentPreview() {
    PainZoneTheme {
        Surface { ProgressScaffold(ProgressUiState.Content(previewExercises), {}, { _, _, _ -> }) }
    }
}
