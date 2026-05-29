package com.painzone.ui.plans.detail

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.painzone.ui.library.LibraryAddEditModal
import com.painzone.ui.theme.PainZoneTheme

@Composable
fun ExercisePickerScreen(
    onBack: () -> Unit,
    onExerciseAdded: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ExercisePickerViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()
    var adding by remember { mutableStateOf<PickerItem?>(null) }
    var showAddNew by remember { mutableStateOf(false) }

    ExercisePickerScaffold(
        state = state,
        query = query,
        onQueryChange = viewModel::onQueryChange,
        onBack = onBack,
        onAddNew = { showAddNew = true },
        onPick = { adding = it },
        modifier = modifier,
    )

    adding?.let { item ->
        ExerciseParamsSheet(
            exerciseName = item.name,
            isDeleted = false,
            // Editable starting point — nothing is persisted until Zapisz.
            initialTargetReps = listOf(10),
            initialRestSeconds = null,
            onSave = { reps, rest ->
                viewModel.addExercise(item.id, reps, rest)
                onExerciseAdded()
            },
            onDismiss = { adding = null },
        )
    }

    if (showAddNew) {
        LibraryAddEditModal(
            onDismiss = { showAddNew = false },
            onSubmit = { name, mg ->
                val result = viewModel.createExercise(name, mg)
                // On success the new exercise flows into the picker list; tap it to add.
                if (result is CreateResult.Success) showAddNew = false
                result
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExercisePickerScaffold(
    state: ExercisePickerUiState,
    query: String,
    onQueryChange: (String) -> Unit,
    onBack: () -> Unit,
    onAddNew: () -> Unit,
    onPick: (PickerItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Dodaj ćwiczenie") },
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
                onClick = onAddNew,
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Nowe ćwiczenie") },
            )
        },
    ) { innerPadding ->
        when (state) {
            ExercisePickerUiState.Loading -> CenteredBody(innerPadding) { CircularProgressIndicator() }
            ExercisePickerUiState.Empty -> MessageBody(
                innerPadding,
                title = "Biblioteka jest pusta",
                body = "Dodaj pierwsze ćwiczenie przyciskiem „+ Nowe ćwiczenie”.",
            )
            is ExercisePickerUiState.Content -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    label = { Text("Szukaj") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                )
                if (state.noResults) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(32.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Brak wyników dla „$query”.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                    }
                } else {
                    GroupedList(sections = state.sections, onPick = onPick)
                }
            }
        }
    }
}

@Composable
private fun GroupedList(sections: List<PickerSection>, onPick: (PickerItem) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        sections.forEach { section ->
            item(key = "header-${section.groupLabel}") {
                Text(
                    text = section.groupLabel,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }
            items(items = section.items, key = { it.id }) { exercise ->
                ListItem(
                    headlineContent = { Text(exercise.name) },
                    modifier = Modifier.clickable { onPick(exercise) },
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            }
        }
    }
}

@Composable
private fun CenteredBody(innerPadding: PaddingValues, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().padding(innerPadding),
        contentAlignment = Alignment.Center,
    ) { content() }
}

@Composable
private fun MessageBody(innerPadding: PaddingValues, title: String, body: String) {
    Box(
        modifier = Modifier.fillMaxSize().padding(innerPadding).padding(32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

private val previewSections = listOf(
    PickerSection("Klatka", listOf(PickerItem(1L, "Wyciskanie sztangi"), PickerItem(2L, "Rozpiętki"))),
    PickerSection("Plecy", listOf(PickerItem(3L, "Martwy ciąg"), PickerItem(4L, "Wiosłowanie"))),
)

@Preview(showBackground = true, name = "Loading")
@Composable
private fun PickerLoadingPreview() {
    PainZoneTheme {
        Surface { ExercisePickerScaffold(ExercisePickerUiState.Loading, "", {}, {}, {}, {}) }
    }
}

@Preview(showBackground = true, name = "Empty library")
@Composable
private fun PickerEmptyPreview() {
    PainZoneTheme {
        Surface { ExercisePickerScaffold(ExercisePickerUiState.Empty, "", {}, {}, {}, {}) }
    }
}

@Preview(showBackground = true, name = "Content")
@Composable
private fun PickerContentPreview() {
    PainZoneTheme {
        Surface {
            ExercisePickerScaffold(
                ExercisePickerUiState.Content(previewSections, noResults = false),
                "", {}, {}, {}, {},
            )
        }
    }
}

@Preview(showBackground = true, name = "No results")
@Composable
private fun PickerNoResultsPreview() {
    PainZoneTheme {
        Surface {
            ExercisePickerScaffold(
                ExercisePickerUiState.Content(emptyList(), noResults = true),
                "xyz", {}, {}, {}, {},
            )
        }
    }
}