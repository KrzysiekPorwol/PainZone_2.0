package com.painzone.ui.library

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.painzone.domain.exercise.CreateResult
import com.painzone.domain.exercise.Exercise
import com.painzone.domain.exercise.MuscleGroup
import com.painzone.ui.theme.PainZoneTheme
import java.time.Instant

@Composable
fun LibraryScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LibraryViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddModal by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        LibraryScaffold(
            state = state,
            onBack = onBack,
            onAddExercise = { showAddModal = true },
        )
        if (showAddModal) {
            LibraryAddEditModal(
                onDismiss = { showAddModal = false },
                onSubmit = { name, mg ->
                    val result = viewModel.addExercise(name, mg)
                    if (result is CreateResult.Success) showAddModal = false
                    result
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LibraryScaffold(
    state: LibraryUiState,
    onBack: () -> Unit,
    onAddExercise: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Biblioteka ćwiczeń") },
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
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddExercise,
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Nowe ćwiczenie") },
            )
        },
    ) { innerPadding ->
        when (state) {
            LibraryUiState.Loading -> LoadingBody(innerPadding)
            LibraryUiState.Empty -> EmptyBody(innerPadding)
            is LibraryUiState.Content -> ContentBody(state.items, innerPadding)
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
                text = "Brak ćwiczeń",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "Dodaj pierwsze ćwiczenie przyciskiem „+ Nowe ćwiczenie” poniżej.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun ContentBody(items: List<Exercise>, innerPadding: PaddingValues) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
    ) {
        items(items = items, key = { it.id }) { exercise ->
            ListItem(
                headlineContent = { Text(exercise.name) },
                supportingContent = { Text(exercise.muscleGroup.labelPl) },
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        }
    }
}

private val previewNow: Instant = Instant.parse("2026-05-27T10:00:00Z")

private val previewExercises = listOf(
    Exercise(1L, "Martwy ciąg", MuscleGroup.Back, previewNow, null),
    Exercise(2L, "Przysiad ze sztangą", MuscleGroup.Legs, previewNow, null),
    Exercise(3L, "Wyciskanie sztangi", MuscleGroup.Chest, previewNow, null),
)

@Preview(showBackground = true, name = "Loading")
@Composable
private fun LibraryScreenLoadingPreview() {
    PainZoneTheme {
        Surface {
            LibraryScaffold(
                state = LibraryUiState.Loading,
                onBack = {},
                onAddExercise = {},
            )
        }
    }
}

@Preview(showBackground = true, name = "Empty")
@Composable
private fun LibraryScreenEmptyPreview() {
    PainZoneTheme {
        Surface {
            LibraryScaffold(
                state = LibraryUiState.Empty,
                onBack = {},
                onAddExercise = {},
            )
        }
    }
}

@Preview(showBackground = true, name = "Content")
@Composable
private fun LibraryScreenContentPreview() {
    PainZoneTheme {
        Surface {
            LibraryScaffold(
                state = LibraryUiState.Content(previewExercises),
                onBack = {},
                onAddExercise = {},
            )
        }
    }
}

@Preview(showBackground = true, name = "Content (1 item)")
@Composable
private fun LibraryScreenContentSinglePreview() {
    PainZoneTheme {
        Surface {
            LibraryScaffold(
                state = LibraryUiState.Content(previewExercises.take(1)),
                onBack = {},
                onAddExercise = {},
            )
        }
    }
}