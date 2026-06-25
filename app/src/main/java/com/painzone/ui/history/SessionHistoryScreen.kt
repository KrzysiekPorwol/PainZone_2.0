package com.painzone.ui.history

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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
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
import com.painzone.ui.theme.PainZoneTheme

@Composable
fun SessionHistoryScreen(
    onBack: () -> Unit,
    onOpenSession: (sessionId: Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SessionHistoryViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val options by viewModel.filterOptions.collectAsStateWithLifecycle()
    val selected by viewModel.selectedFilter.collectAsStateWithLifecycle()
    SessionHistoryScaffold(
        state = state,
        options = options,
        selectedPlan = selected,
        onSelectFilter = viewModel::selectFilter,
        onBack = onBack,
        onOpenSession = onOpenSession,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SessionHistoryScaffold(
    state: SessionHistoryUiState,
    options: List<PlanFilterOption>,
    selectedPlan: String?,
    onSelectFilter: (String?) -> Unit,
    onBack: () -> Unit,
    onOpenSession: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Historia") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            PlanFilterDropdown(
                options = options,
                selectedPlan = selectedPlan,
                onSelectFilter = onSelectFilter,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            )
            when (state) {
                SessionHistoryUiState.Loading -> CenterBox { CircularProgressIndicator() }
                SessionHistoryUiState.Empty -> EmptyBody(planFiltered = selectedPlan != null)
                is SessionHistoryUiState.Content -> SessionList(state.sessions, onOpenSession)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlanFilterDropdown(
    options: List<PlanFilterOption>,
    selectedPlan: String?,
    onSelectFilter: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedLabel = options.firstOrNull { it.name == selectedPlan }?.label
        ?: ALL_PLANS_FILTER.label
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = selectedLabel,
            onValueChange = {},
            readOnly = true,
            label = { Text("Plan") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.label) },
                    onClick = {
                        onSelectFilter(option.name)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun CenterBox(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) { content() }
}

@Composable
private fun EmptyBody(planFiltered: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (planFiltered) "Brak sesji dla tego planu" else "Brak sesji",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun SessionList(
    sessions: List<SessionCardUi>,
    onOpenSession: (Long) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(items = sessions, key = { it.sessionId }) { session ->
            SessionCard(session, onOpenSession)
        }
    }
}

@Composable
private fun SessionCard(session: SessionCardUi, onOpenSession: (Long) -> Unit) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenSession(session.sessionId) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = session.title,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = session.stats,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private val previewOptions = listOf(
    ALL_PLANS_FILTER,
    PlanFilterOption("Push/Pull/Legs", "Push/Pull/Legs"),
    PlanFilterOption("Full Body", "Full Body"),
)

private val previewSessions = listOf(
    SessionCardUi(1L, "24.06 · Push/Pull/Legs · Push A", "18 serii · tonaż 4250 kg"),
    SessionCardUi(2L, "22.06 · Push/Pull/Legs · Pull A", "15 serii · tonaż 3890 kg"),
    SessionCardUi(3L, "20.06 · Full Body · Dzień 1", "12 serii · tonaż 2750 kg"),
)

@Preview(showBackground = true, name = "Loading")
@Composable
private fun SessionHistoryLoadingPreview() {
    PainZoneTheme {
        Surface {
            SessionHistoryScaffold(SessionHistoryUiState.Loading, previewOptions, null, {}, {}, {})
        }
    }
}

@Preview(showBackground = true, name = "Empty")
@Composable
private fun SessionHistoryEmptyPreview() {
    PainZoneTheme {
        Surface {
            SessionHistoryScaffold(SessionHistoryUiState.Empty, previewOptions, "Full Body", {}, {}, {})
        }
    }
}

@Preview(showBackground = true, name = "Content")
@Composable
private fun SessionHistoryContentPreview() {
    PainZoneTheme {
        Surface {
            SessionHistoryScaffold(
                SessionHistoryUiState.Content(previewSessions),
                previewOptions,
                null,
                {},
                {},
                {},
            )
        }
    }
}
