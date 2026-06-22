package com.painzone.ui.session

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.SwapVerticalCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.painzone.domain.exercise.MuscleGroup
import com.painzone.ui.library.labelPl
import com.painzone.ui.theme.PainZoneTheme

@Composable
fun SessionScreen(
    onExit: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SessionViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.finished.collect { onExit() }
    }

    SessionScaffold(
        state = state,
        onExit = onExit,
        onFinish = viewModel::finishSession,
        onSelectExercise = viewModel::selectExercise,
        onNext = viewModel::nextExercise,
        onPrevious = viewModel::previousExercise,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SessionScaffold(
    state: SessionUiState,
    onExit: () -> Unit,
    onFinish: () -> Unit,
    onSelectExercise: (Int) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showJumpSheet by remember { mutableStateOf(false) }
    var showFinishDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            SessionTopBar(
                state = state,
                onExit = onExit,
                onJump = { showJumpSheet = true },
                onFinish = { showFinishDialog = true },
            )
        },
    ) { innerPadding ->
        when (state) {
            SessionUiState.Loading -> CenteredBody(innerPadding) { CircularProgressIndicator() }
            SessionUiState.NotFound -> NotFoundBody(innerPadding)
            is SessionUiState.Content -> SessionBody(
                state = state,
                innerPadding = innerPadding,
                onNext = onNext,
                onPrevious = onPrevious,
                onFinish = { showFinishDialog = true },
            )
        }
    }

    if (showFinishDialog) {
        FinishSessionDialog(
            onConfirm = {
                showFinishDialog = false
                onFinish()
            },
            onDismiss = { showFinishDialog = false },
        )
    }

    if (showJumpSheet && state is SessionUiState.Content) {
        ExerciseJumpSheet(
            sheetState = rememberModalBottomSheetState(),
            exercises = state.exercises,
            activeIndex = state.activeIndex,
            onSelect = {
                onSelectExercise(it)
                showJumpSheet = false
            },
            onDismiss = { showJumpSheet = false },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SessionTopBar(
    state: SessionUiState,
    onExit: () -> Unit,
    onJump: () -> Unit,
    onFinish: () -> Unit,
) {
    var menuExpanded by remember { mutableStateOf(false) }
    TopAppBar(
        title = {
            if (state is SessionUiState.Content) {
                Column {
                    Text(state.dayName, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "${state.planName} · Ćwiczenie ${state.position}/${state.exerciseCount}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                Text("Sesja", style = MaterialTheme.typography.titleMedium)
            }
        },
        navigationIcon = {
            IconButton(onClick = onExit) {
                Icon(Icons.Filled.Close, contentDescription = "Zamknij sesję")
            }
        },
        actions = {
            if (state is SessionUiState.Content) {
                IconButton(onClick = onJump) {
                    Icon(
                        imageVector = Icons.Filled.SwapVerticalCircle,
                        contentDescription = "Skocz do ćwiczenia",
                    )
                }
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(Icons.Filled.MoreVert, contentDescription = "Więcej")
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                ) {
                    DropdownMenuItem(
                        text = { Text("Zakończ sesję") },
                        onClick = {
                            menuExpanded = false
                            onFinish()
                        },
                    )
                }
            }
        },
    )
}

@Composable
private fun SessionBody(
    state: SessionUiState.Content,
    innerPadding: PaddingValues,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onFinish: () -> Unit,
) {
    val exercise = state.activeExercise
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ActiveExerciseCard(exercise)

        // Logging input, Last Set Preview and Rest Timer land in M3.5–M3.7.
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
        ) {
            Text(
                text = "Logowanie serii pojawi się w kolejnym kroku.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp),
            )
        }

        Box(modifier = Modifier.weight(1f))

        ExerciseNavRow(
            state = state,
            onNext = onNext,
            onPrevious = onPrevious,
            onFinish = onFinish,
        )
    }
}

@Composable
private fun ActiveExerciseCard(exercise: SessionExerciseUi) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(exercise.name, style = MaterialTheme.typography.headlineSmall)
            Text(
                text = exercise.muscleGroup.labelPl,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = setProgressLabel(exercise),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp),
            )
            Text(
                text = restLabel(exercise.plannedRestSeconds),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ExerciseNavRow(
    state: SessionUiState.Content,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onFinish: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (state.hasPrevious) {
            OutlinedButton(onClick = onPrevious, modifier = Modifier.weight(1f)) {
                Text("← Poprzednie")
            }
        }
        if (state.hasNext) {
            OutlinedButton(
                onClick = onNext,
                modifier = Modifier.weight(1f),
            ) {
                Text("Następne")
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.padding(start = 4.dp),
                )
            }
        } else {
            // Last exercise: finishing here is the natural end. Mid-session finish
            // is also available from the top-bar menu. Full D2 summary lands in M3.10.
            Button(onClick = onFinish, modifier = Modifier.weight(1f)) {
                Text("Zakończ sesję")
            }
        }
    }
}

@Composable
private fun FinishSessionDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Zakończyć sesję?") },
        text = { Text("Sesja zostanie zapisana jako zakończona. Możesz to zrobić nawet w połowie planu.") },
        confirmButton = { TextButton(onClick = onConfirm) { Text("Zakończ") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Anuluj") } },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExerciseJumpSheet(
    sheetState: androidx.compose.material3.SheetState,
    exercises: List<SessionExerciseUi>,
    activeIndex: Int,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Text(
            text = "Ćwiczenia sesji",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
        )
        LazyColumn {
            itemsIndexed(exercises, key = { _, ex -> ex.snapshotId }) { index, ex ->
                val active = index == activeIndex
                ListItem(
                    headlineContent = { Text("${index + 1}. ${ex.name}") },
                    supportingContent = {
                        Text("${ex.loggedSetCount}/${ex.plannedSets} serii · ${ex.muscleGroup.labelPl}")
                    },
                    colors = if (active) {
                        ListItemDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        )
                    } else {
                        ListItemDefaults.colors()
                    },
                    modifier = Modifier.clickable { onSelect(index) },
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
private fun NotFoundBody(innerPadding: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Sesja nie jest już dostępna.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

// "Seria K/L · cel R" — K = next set number, L = planned sets, R = target reps for that set.
private fun setProgressLabel(exercise: SessionExerciseUi): String {
    val nextSet = (exercise.loggedSetCount + 1).coerceAtMost(exercise.plannedSets)
    val targetReps = exercise.plannedTargetReps.getOrElse(exercise.loggedSetCount) {
        exercise.plannedTargetReps.last()
    }
    return "Seria $nextSet/${exercise.plannedSets} · cel $targetReps powt."
}

private fun restLabel(restSeconds: Int?): String = when {
    restSeconds == null -> "Odpoczynek: bez limitu"
    restSeconds % 60 == 0 -> "Odpoczynek: ${restSeconds / 60} min"
    restSeconds < 60 -> "Odpoczynek: ${restSeconds}s"
    else -> "Odpoczynek: ${restSeconds / 60} min ${restSeconds % 60}s"
}

private val previewExercises = listOf(
    SessionExerciseUi(1L, "Wyciskanie sztangi", MuscleGroup.Chest, listOf(10, 9, 8), 90, 1),
    SessionExerciseUi(2L, "Rozpiętki hantlami", MuscleGroup.Chest, listOf(12, 12, 12), 60, 0),
    SessionExerciseUi(3L, "Wyciskanie francuskie", MuscleGroup.Triceps, listOf(10, 10), null, 0),
)

@Preview(showBackground = true, name = "Loading")
@Composable
private fun SessionLoadingPreview() {
    PainZoneTheme {
        Surface { SessionScaffold(SessionUiState.Loading, {}, {}, {}, {}, {}) }
    }
}

@Preview(showBackground = true, name = "Not found")
@Composable
private fun SessionNotFoundPreview() {
    PainZoneTheme {
        Surface { SessionScaffold(SessionUiState.NotFound, {}, {}, {}, {}, {}) }
    }
}

@Preview(showBackground = true, name = "Content — first exercise")
@Composable
private fun SessionContentFirstPreview() {
    PainZoneTheme {
        Surface {
            SessionScaffold(
                SessionUiState.Content("Push/Pull/Legs", "Push", previewExercises, 0),
                {}, {}, {}, {}, {},
            )
        }
    }
}

@Preview(showBackground = true, name = "Content — last exercise")
@Composable
private fun SessionContentLastPreview() {
    PainZoneTheme {
        Surface {
            SessionScaffold(
                SessionUiState.Content("Push/Pull/Legs", "Push", previewExercises, 2),
                {}, {}, {}, {}, {},
            )
        }
    }
}
