package com.painzone.ui.plans.detail

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.SwapVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.painzone.ui.theme.PainZoneTheme
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun DayDetailScreen(
    dayName: String,
    onBack: () -> Unit,
    onAddExercise: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DayDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var editingRow by remember { mutableStateOf<PlannedExerciseRow?>(null) }

    LaunchedEffect(Unit) {
        viewModel.snackbarEvents.collect { snackbarHostState.showSnackbar(it) }
    }

    DayDetailScaffold(
        dayName = dayName,
        state = state,
        snackbarHostState = snackbarHostState,
        onBack = onBack,
        onAddExercise = onAddExercise,
        onEditParams = { editingRow = it },
        onRemove = { viewModel.removeExercise(it.plannedExerciseId, it.name) },
        onReorder = viewModel::reorder,
        modifier = modifier,
    )

    editingRow?.let { row ->
        ExerciseParamsSheet(
            exerciseName = row.name,
            isDeleted = row.isDeleted,
            initialTargetReps = row.targetReps,
            initialRestSeconds = row.restSeconds,
            onSave = { reps, rest -> viewModel.updateParams(row.plannedExerciseId, reps, rest) },
            onDismiss = { editingRow = null },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DayDetailScaffold(
    dayName: String,
    state: DayDetailUiState,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onAddExercise: () -> Unit,
    onEditParams: (PlannedExerciseRow) -> Unit,
    onRemove: (PlannedExerciseRow) -> Unit,
    onReorder: (List<Long>) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(dayName) },
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
                text = { Text("Dodaj ćwiczenie") },
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        when (state) {
            DayDetailUiState.Loading -> CenteredBody(innerPadding) { CircularProgressIndicator() }
            is DayDetailUiState.Content ->
                if (state.rows.isEmpty()) {
                    EmptyBody(innerPadding)
                } else {
                    ExerciseList(
                        rows = state.rows,
                        innerPadding = innerPadding,
                        onEditParams = onEditParams,
                        onRemove = onRemove,
                        onReorder = onReorder,
                    )
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
                text = "Brak ćwiczeń",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "Dodaj pierwsze ćwiczenie przyciskiem „+ Dodaj ćwiczenie” poniżej.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun ExerciseList(
    rows: List<PlannedExerciseRow>,
    innerPadding: PaddingValues,
    onEditParams: (PlannedExerciseRow) -> Unit,
    onRemove: (PlannedExerciseRow) -> Unit,
    onReorder: (List<Long>) -> Unit,
) {
    val haptic = LocalHapticFeedback.current
    val lazyListState = rememberLazyListState()
    // Local working copy reordered live during drag; resynced whenever Room re-emits.
    var ordered by remember { mutableStateOf(rows) }
    LaunchedEffect(rows) { ordered = rows }

    val reorderState = rememberReorderableLazyListState(lazyListState) { from, to ->
        ordered = ordered.toMutableList().apply { add(to.index, removeAt(from.index)) }
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    LazyColumn(
        state = lazyListState,
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
    ) {
        items(items = ordered, key = { it.plannedExerciseId }) { row ->
            ReorderableItem(reorderState, key = row.plannedExerciseId) { isDragging ->
                val elevation by animateDpAsState(if (isDragging) 4.dp else 0.dp, label = "dragElevation")
                Surface(shadowElevation = elevation) {
                    ListItem(
                        headlineContent = { Text(row.name) },
                        supportingContent = { Text(subtitle(row)) },
                        trailingContent = {
                            Row {
                                IconButton(onClick = { onRemove(row) }) {
                                    Icon(
                                        imageVector = Icons.Filled.Delete,
                                        contentDescription = "Usuń ${row.name}",
                                    )
                                }
                                IconButton(
                                    modifier = Modifier.draggableHandle(
                                        onDragStopped = { onReorder(ordered.map { it.plannedExerciseId }) },
                                    ),
                                    onClick = {}, // handle only drives drag, not a tap action
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.SwapVert,
                                        contentDescription = "Zmień kolejność: ${row.name}",
                                    )
                                }
                            }
                        },
                        modifier = Modifier.clickable { onEditParams(row) },
                    )
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            }
        }
    }
}

private fun subtitle(row: PlannedExerciseRow): String {
    val sets = row.targetReps.size
    val rest = if (row.restSeconds == null) "bez odpoczynku" else "odp. ${formatRest(row.restSeconds)}"
    return "$sets ${seriePlural(sets)} · $rest"
}

private fun seriePlural(n: Int): String {
    val mod10 = n % 10
    val mod100 = n % 100
    return when {
        n == 1 -> "seria"
        mod10 in 2..4 && mod100 !in 12..14 -> "serie"
        else -> "serii"
    }
}

private val previewRows = listOf(
    PlannedExerciseRow(1L, 10L, "Wyciskanie sztangi", false, listOf(10, 9, 8), 90),
    PlannedExerciseRow(2L, 11L, "Rozpiętki", false, listOf(12, 12, 12), null),
    PlannedExerciseRow(3L, 12L, "Ćwiczenie usunięte", true, listOf(10), 60),
)

@Preview(showBackground = true, name = "Loading")
@Composable
private fun DayDetailLoadingPreview() {
    PainZoneTheme {
        Surface {
            DayDetailScaffold(
                dayName = "Push",
                state = DayDetailUiState.Loading,
                snackbarHostState = remember { SnackbarHostState() },
                onBack = {}, onAddExercise = {}, onEditParams = {}, onRemove = {}, onReorder = {},
            )
        }
    }
}

@Preview(showBackground = true, name = "Empty")
@Composable
private fun DayDetailEmptyPreview() {
    PainZoneTheme {
        Surface {
            DayDetailScaffold(
                dayName = "Push",
                state = DayDetailUiState.Content(emptyList()),
                snackbarHostState = remember { SnackbarHostState() },
                onBack = {}, onAddExercise = {}, onEditParams = {}, onRemove = {}, onReorder = {},
            )
        }
    }
}

@Preview(showBackground = true, name = "Content")
@Composable
private fun DayDetailContentPreview() {
    PainZoneTheme {
        Surface {
            DayDetailScaffold(
                dayName = "Push",
                state = DayDetailUiState.Content(previewRows),
                snackbarHostState = remember { SnackbarHostState() },
                onBack = {}, onAddExercise = {}, onEditParams = {}, onRemove = {}, onReorder = {},
            )
        }
    }
}