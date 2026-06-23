package com.painzone.ui.session

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.SwapVerticalCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.painzone.domain.exercise.MuscleGroup
import com.painzone.domain.session.Rpe
import com.painzone.ui.library.labelPl
import com.painzone.ui.theme.PainZoneTheme

// Action handlers for the input row — bundled so previews and the scaffold stay readable.
data class SessionInputCallbacks(
    val onRepsChange: (String) -> Unit,
    val onWeightChange: (String) -> Unit,
    val onRepsIncrement: () -> Unit,
    val onRepsDecrement: () -> Unit,
    val onWeightIncrement: () -> Unit,
    val onWeightDecrement: () -> Unit,
    val onRpeSelect: (Rpe) -> Unit,
    val onSave: () -> Unit,
    val onEditSet: (Long) -> Unit,
    val onCancelEdit: () -> Unit,
) {
    companion object {
        val Noop = SessionInputCallbacks({}, {}, {}, {}, {}, {}, {}, {}, {}, {})
    }
}

@Composable
fun SessionScreen(
    onExit: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SessionViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val input by viewModel.input.collectAsStateWithLifecycle()
    val restTimer by viewModel.restTimer.collectAsStateWithLifecycle()
    val repsFocus = remember { FocusRequester() }
    val fireRestAlert = rememberRestAlerter()

    LaunchedEffect(Unit) {
        viewModel.finished.collect { onExit() }
    }
    LaunchedEffect(Unit) {
        viewModel.restOverflow.collect { fireRestAlert() }
    }
    LaunchedEffect(Unit) {
        viewModel.focusReps.collect {
            // Set may not be attached yet on first emission; ignore if so.
            runCatching { repsFocus.requestFocus() }
        }
    }

    SessionScaffold(
        state = state,
        input = input,
        restTimer = restTimer,
        repsFocus = repsFocus,
        onExit = onExit,
        onFinish = viewModel::finishSession,
        onSelectExercise = viewModel::selectExercise,
        onNext = viewModel::nextExercise,
        onPrevious = viewModel::previousExercise,
        inputCallbacks = SessionInputCallbacks(
            onRepsChange = viewModel::updateReps,
            onWeightChange = viewModel::updateWeight,
            onRepsIncrement = viewModel::incrementReps,
            onRepsDecrement = viewModel::decrementReps,
            onWeightIncrement = viewModel::incrementWeight,
            onWeightDecrement = viewModel::decrementWeight,
            onRpeSelect = viewModel::selectRpe,
            onSave = viewModel::saveSet,
            onEditSet = viewModel::editSet,
            onCancelEdit = viewModel::cancelEdit,
        ),
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SessionScaffold(
    state: SessionUiState,
    input: SetInputUi,
    restTimer: RestTimerUi?,
    repsFocus: FocusRequester,
    onExit: () -> Unit,
    onFinish: () -> Unit,
    onSelectExercise: (Int) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    inputCallbacks: SessionInputCallbacks,
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
                input = input,
                restTimer = restTimer,
                repsFocus = repsFocus,
                innerPadding = innerPadding,
                onNext = onNext,
                onPrevious = onPrevious,
                onFinish = { showFinishDialog = true },
                inputCallbacks = inputCallbacks,
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
    input: SetInputUi,
    restTimer: RestTimerUi?,
    repsFocus: FocusRequester,
    innerPadding: PaddingValues,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onFinish: () -> Unit,
    inputCallbacks: SessionInputCallbacks,
) {
    val exercise = state.activeExercise
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp)
            .imePadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ActiveExerciseCard(exercise)

        LogInputCard(
            input = input,
            repsFocus = repsFocus,
            callbacks = inputCallbacks,
            // All planned sets logged → swap the save action for advancing (S9 auto-advance CTA),
            // so the user can't blindly keep logging beyond the plan. Editing a set stays available.
            isComplete = exercise.loggedSetCount >= exercise.plannedSets,
            hasNext = state.hasNext,
            onAdvance = if (state.hasNext) onNext else onFinish,
        )

        // Logged sets fill the remaining space; reverse-chrono with the fresh set tappable.
        LoggedSetList(
            exercise = exercise,
            editingSetId = input.editingSetId,
            onEditSet = inputCallbacks.onEditSet,
            modifier = Modifier.weight(1f),
        )

        ExerciseNavRow(
            state = state,
            onNext = onNext,
            onPrevious = onPrevious,
            onFinish = onFinish,
        )

        if (restTimer != null) {
            RestTimerBanner(restTimer)
        }
    }
}

@Composable
private fun RestTimerBanner(timer: RestTimerUi) {
    // Over-target gets an accent here; the one-shot haptic/sound alert fires from restOverflow.
    val container =
        if (timer.isOverTarget) MaterialTheme.colorScheme.tertiaryContainer
        else MaterialTheme.colorScheme.surfaceVariant
    val onContainer =
        if (timer.isOverTarget) MaterialTheme.colorScheme.onTertiaryContainer
        else MaterialTheme.colorScheme.onSurfaceVariant
    Surface(
        color = container,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Odpoczynek",
                style = MaterialTheme.typography.labelLarge,
                color = onContainer,
            )
            val target = timer.targetSeconds?.let { " / ${formatRestClock(it)}" }.orEmpty()
            Text(
                text = "${formatRestClock(timer.elapsedSeconds)}$target",
                style = MaterialTheme.typography.titleMedium,
                color = onContainer,
            )
        }
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
            LastSetPreviewLine(exercise)
        }
    }
}

@Composable
private fun LastSetPreviewLine(exercise: SessionExerciseUi) {
    // No prior session at all → first-time copy. Prior session present but this series wasn't
    // logged then (fewer sets last time) → nothing to compare, so render nothing.
    val text = when {
        !exercise.hasPriorSession -> "Tym planem trenujesz 1 raz."
        exercise.currentLastSet != null -> "Ostatnio: ${lastSetPreviewLine(exercise.currentLastSet!!)}"
        else -> return
    }
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 4.dp),
    )
}

@Composable
private fun LogInputCard(
    input: SetInputUi,
    repsFocus: FocusRequester,
    callbacks: SessionInputCallbacks,
    isComplete: Boolean,
    hasNext: Boolean,
    onAdvance: () -> Unit,
) {
    // When the exercise is done we replace the input with an advance CTA — but editing the
    // fresh set (tap in the list) re-opens the form even after completion.
    if (isComplete && !input.isEditing) {
        ExerciseCompleteCard(hasNext = hasNext, onAdvance = onAdvance)
        return
    }
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StepperField(
                    label = "Powtórzenia",
                    value = input.reps,
                    onValueChange = callbacks.onRepsChange,
                    onIncrement = callbacks.onRepsIncrement,
                    onDecrement = callbacks.onRepsDecrement,
                    focusRequester = repsFocus,
                    modifier = Modifier.weight(1f),
                )
                StepperField(
                    label = "Ciężar (kg)",
                    value = input.weight,
                    onValueChange = callbacks.onWeightChange,
                    onIncrement = callbacks.onWeightIncrement,
                    onDecrement = callbacks.onWeightDecrement,
                    decimal = true,
                    modifier = Modifier.weight(1f),
                )
            }

            RpeChips(selected = input.rpe, onSelect = callbacks.onRpeSelect)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (input.isEditing) {
                    OutlinedButton(onClick = callbacks.onCancelEdit, modifier = Modifier.weight(1f)) {
                        Text("Anuluj")
                    }
                }
                Button(
                    onClick = callbacks.onSave,
                    enabled = input.canSave,
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                    Text(if (input.isEditing) "Zapisz zmiany" else "Zapisz serię")
                }
            }
        }
    }
}

@Composable
private fun ExerciseCompleteCard(hasNext: Boolean, onAdvance: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Wszystkie serie zapisane. Edytuj ostatnią poniżej lub przejdź dalej.",
                style = MaterialTheme.typography.bodyMedium,
            )
            Button(onClick = onAdvance, modifier = Modifier.fillMaxWidth()) {
                if (hasNext) {
                    Text("Następne ćwiczenie")
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.padding(start = 4.dp),
                    )
                } else {
                    Text("Zakończ sesję")
                }
            }
        }
    }
}

@Composable
private fun StepperField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier,
    decimal: Boolean = false,
    focusRequester: FocusRequester? = null,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onDecrement) {
                Icon(Icons.Filled.Remove, contentDescription = "Mniej $label")
            }
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = if (decimal) KeyboardType.Decimal else KeyboardType.Number,
                ),
                modifier = Modifier
                    .weight(1f)
                    .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier),
            )
            IconButton(onClick = onIncrement) {
                Icon(Icons.Filled.Add, contentDescription = "Więcej $label")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RpeChips(selected: Rpe?, onSelect: (Rpe) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Rpe.entries.forEach { rpe ->
            FilterChip(
                selected = selected == rpe,
                onClick = { onSelect(rpe) },
                label = { Text(rpe.labelPl) },
            )
        }
    }
}

@Composable
private fun LoggedSetList(
    exercise: SessionExerciseUi,
    editingSetId: Long?,
    onEditSet: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (exercise.loggedSets.isEmpty()) {
        Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
            Text(
                text = "Brak serii — zaloguj pierwszą powyżej.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
        return
    }
    val freshId = exercise.freshSetId
    // Reverse-chrono: freshest set on top.
    val reversed = exercise.loggedSets.sortedByDescending { it.order }
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(reversed, key = { it.id }) { set ->
            val editable = set.id == freshId
            LoggedSetRow(
                set = set,
                editable = editable,
                editing = set.id == editingSetId,
                onClick = { onEditSet(set.id) },
            )
        }
    }
}

@Composable
private fun LoggedSetRow(
    set: LoggedSetUi,
    editable: Boolean,
    editing: Boolean,
    onClick: () -> Unit,
) {
    val rpeSuffix = set.rpe?.let { " / ${it.labelPl}" }.orEmpty()
    ListItem(
        headlineContent = { Text("Seria ${set.order}") },
        supportingContent = { Text("${set.reps} × ${formatWeight(set.weight)} kg$rpeSuffix") },
        trailingContent = if (editable) { { Text("edytuj") } } else null,
        colors = if (editing) {
            ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        } else {
            ListItemDefaults.colors()
        },
        modifier = if (editable) Modifier.clickable(onClick = onClick) else Modifier,
    )
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
    SessionExerciseUi(
        snapshotId = 1L,
        exerciseId = 10L,
        name = "Wyciskanie sztangi",
        muscleGroup = MuscleGroup.Chest,
        plannedTargetReps = listOf(10, 9, 8),
        plannedRestSeconds = 90,
        loggedSets = listOf(
            LoggedSetUi(1L, 1, 10, 60.0, Rpe.Normal),
            LoggedSetUi(2L, 2, 9, 62.5, Rpe.Hard),
        ),
        lastSession = listOf(
            LastSetPreviewUi(reps = 10, weight = 57.5, rpe = Rpe.Normal, daysAgo = 3),
            LastSetPreviewUi(reps = 9, weight = 60.0, rpe = Rpe.Hard, daysAgo = 3),
            LastSetPreviewUi(reps = 8, weight = 60.0, rpe = Rpe.Hard, daysAgo = 3),
        ),
    ),
    SessionExerciseUi(
        snapshotId = 2L,
        exerciseId = 11L,
        name = "Rozpiętki hantlami",
        muscleGroup = MuscleGroup.Chest,
        plannedTargetReps = listOf(12, 12, 12),
        plannedRestSeconds = 60,
        loggedSets = emptyList(),
        lastSession = emptyList(),
    ),
    SessionExerciseUi(
        snapshotId = 3L,
        exerciseId = 12L,
        name = "Wyciskanie francuskie",
        muscleGroup = MuscleGroup.Triceps,
        plannedTargetReps = listOf(10, 10),
        plannedRestSeconds = null,
        loggedSets = emptyList(),
    ),
)

@Composable
private fun previewScaffold(
    state: SessionUiState,
    input: SetInputUi = SetInputUi(reps = "10", weight = "62.5"),
    restTimer: RestTimerUi? = null,
) {
    PainZoneTheme {
        Surface {
            SessionScaffold(
                state = state,
                input = input,
                restTimer = restTimer,
                repsFocus = remember { FocusRequester() },
                onExit = {},
                onFinish = {},
                onSelectExercise = {},
                onNext = {},
                onPrevious = {},
                inputCallbacks = SessionInputCallbacks.Noop,
            )
        }
    }
}

@Preview(showBackground = true, name = "Loading")
@Composable
private fun SessionLoadingPreview() = previewScaffold(SessionUiState.Loading)

@Preview(showBackground = true, name = "Not found")
@Composable
private fun SessionNotFoundPreview() = previewScaffold(SessionUiState.NotFound)

@Preview(showBackground = true, name = "Content — logging (2 sets done)")
@Composable
private fun SessionContentFirstPreview() =
    previewScaffold(SessionUiState.Content("Push/Pull/Legs", "Push", previewExercises, 0))

@Preview(showBackground = true, name = "Content — empty exercise")
@Composable
private fun SessionContentEmptyExercisePreview() =
    previewScaffold(SessionUiState.Content("Push/Pull/Legs", "Push", previewExercises, 1))

@Preview(showBackground = true, name = "Content — editing fresh set")
@Composable
private fun SessionContentEditingPreview() =
    previewScaffold(
        SessionUiState.Content("Push/Pull/Legs", "Push", previewExercises, 0),
        input = SetInputUi(reps = "9", weight = "62.5", rpe = Rpe.Hard, editingSetId = 2L),
    )

@Preview(showBackground = true, name = "Content — last exercise")
@Composable
private fun SessionContentLastPreview() =
    previewScaffold(SessionUiState.Content("Push/Pull/Legs", "Push", previewExercises, 2))

@Preview(showBackground = true, name = "Content — rest timer running")
@Composable
private fun SessionContentRestTimerPreview() =
    previewScaffold(
        SessionUiState.Content("Push/Pull/Legs", "Push", previewExercises, 0),
        restTimer = RestTimerUi(elapsedSeconds = 65, targetSeconds = 90),
    )

@Preview(showBackground = true, name = "Content — rest timer over target")
@Composable
private fun SessionContentRestTimerOverPreview() =
    previewScaffold(
        SessionUiState.Content("Push/Pull/Legs", "Push", previewExercises, 0),
        restTimer = RestTimerUi(elapsedSeconds = 105, targetSeconds = 90),
    )

@Preview(showBackground = true, name = "Content — exercise complete (advance CTA)")
@Composable
private fun SessionContentCompletePreview() {
    val complete = previewExercises[0].copy(
        loggedSets = listOf(
            LoggedSetUi(1L, 1, 10, 60.0, Rpe.Normal),
            LoggedSetUi(2L, 2, 9, 62.5, Rpe.Hard),
            LoggedSetUi(3L, 3, 8, 62.5, null),
        ),
    )
    previewScaffold(
        SessionUiState.Content("Push/Pull/Legs", "Push", listOf(complete) + previewExercises.drop(1), 0),
    )
}
