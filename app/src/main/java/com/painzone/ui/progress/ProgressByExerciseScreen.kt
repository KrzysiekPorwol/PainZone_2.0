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
import com.painzone.domain.exercise.Exercise
import com.painzone.domain.exercise.MuscleGroup
import com.painzone.ui.library.labelPl
import com.painzone.ui.theme.PainZoneTheme
import java.time.Instant

@Composable
fun ProgressByExerciseScreen(
    onBack: () -> Unit,
    onOpenStats: (exerciseId: Long, exerciseName: String, muscleGroupLabel: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProgressByExerciseViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ProgressByExerciseScaffold(
        state = state,
        onBack = onBack,
        onOpenStats = onOpenStats,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProgressByExerciseScaffold(
    state: ProgressByExerciseUiState,
    onBack: () -> Unit,
    onOpenStats: (Long, String, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Po ćwiczeniu") },
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
            ProgressByExerciseUiState.Loading -> CenterBox(innerPadding) { CircularProgressIndicator() }
            ProgressByExerciseUiState.Empty -> EmptyBody(innerPadding)
            is ProgressByExerciseUiState.Content -> ExerciseList(state.exercises, innerPadding, onOpenStats)
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
                text = "Brak ćwiczeń",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "Dodaj ćwiczenie w bibliotece, aby śledzić postęp.",
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
private fun ProgressByExerciseLoadingPreview() {
    PainZoneTheme {
        Surface { ProgressByExerciseScaffold(ProgressByExerciseUiState.Loading, {}, { _, _, _ -> }) }
    }
}

@Preview(showBackground = true, name = "Empty")
@Composable
private fun ProgressByExerciseEmptyPreview() {
    PainZoneTheme {
        Surface { ProgressByExerciseScaffold(ProgressByExerciseUiState.Empty, {}, { _, _, _ -> }) }
    }
}

@Preview(showBackground = true, name = "Content")
@Composable
private fun ProgressByExerciseContentPreview() {
    PainZoneTheme {
        Surface {
            ProgressByExerciseScaffold(
                ProgressByExerciseUiState.Content(previewExercises),
                {},
                { _, _, _ -> },
            )
        }
    }
}
