package com.painzone.ui.progress

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.painzone.ui.common.TopLevelTopBar
import com.painzone.ui.theme.PainZoneTheme

@Composable
fun ProgressHubScreen(
    onManageLibrary: () -> Unit,
    onByExercise: () -> Unit,
    onByPlan: () -> Unit,
    onChronological: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProgressHubViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ProgressHubScaffold(
        state = state,
        onManageLibrary = onManageLibrary,
        onByExercise = onByExercise,
        onByPlan = onByPlan,
        onChronological = onChronological,
        modifier = modifier,
    )
}

@Composable
private fun ProgressHubScaffold(
    state: ProgressHubUiState,
    onManageLibrary: () -> Unit,
    onByExercise: () -> Unit,
    onByPlan: () -> Unit,
    onChronological: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = { TopLevelTopBar(title = "Postęp", onManageLibrary = onManageLibrary) },
    ) { innerPadding ->
        when (state) {
            ProgressHubUiState.Loading -> CenterBox(innerPadding) { CircularProgressIndicator() }
            ProgressHubUiState.Empty -> EmptyBody(innerPadding)
            ProgressHubUiState.Ready -> HubChoices(innerPadding, onByExercise, onByPlan, onChronological)
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
private fun HubChoices(
    innerPadding: PaddingValues,
    onByExercise: () -> Unit,
    onByPlan: () -> Unit,
    onChronological: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        HubCard(
            icon = Icons.Filled.FitnessCenter,
            title = "Po ćwiczeniu",
            subtitle = "Historia serii dla wybranego ćwiczenia",
            onClick = onByExercise,
        )
        HubCard(
            icon = Icons.AutoMirrored.Filled.TrendingUp,
            title = "Po planie",
            subtitle = "Sesje pogrupowane planem treningowym",
            onClick = onByPlan,
        )
        HubCard(
            icon = Icons.Filled.CalendarMonth,
            title = "Chronologicznie",
            subtitle = "Wszystkie sesje od najnowszej",
            onClick = onChronological,
        )
    }
}

@Composable
private fun HubCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
) {
    val contentAlpha = if (enabled) 1f else 0.38f
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary.copy(alpha = contentAlpha),
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = contentAlpha),
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = contentAlpha),
                )
            }
            if (!enabled) {
                Text(
                    text = "Wkrótce",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = contentAlpha),
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Loading")
@Composable
private fun ProgressHubLoadingPreview() {
    PainZoneTheme {
        Surface { ProgressHubScaffold(ProgressHubUiState.Loading, {}, {}, {}, {}) }
    }
}

@Preview(showBackground = true, name = "Empty")
@Composable
private fun ProgressHubEmptyPreview() {
    PainZoneTheme {
        Surface { ProgressHubScaffold(ProgressHubUiState.Empty, {}, {}, {}, {}) }
    }
}

@Preview(showBackground = true, name = "Ready")
@Composable
private fun ProgressHubReadyPreview() {
    PainZoneTheme {
        Surface { ProgressHubScaffold(ProgressHubUiState.Ready, {}, {}, {}, {}) }
    }
}
