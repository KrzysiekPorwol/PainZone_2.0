package com.painzone.ui.plans

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.painzone.domain.plan.DeletePlanResult
import com.painzone.domain.plan.PlanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class PlansViewModel @Inject constructor(
    private val repository: PlanRepository,
) : ViewModel() {

    val uiState: StateFlow<PlansUiState> = repository.observeSummaries()
        .map { summaries ->
            if (summaries.isEmpty()) PlansUiState.Empty
            else PlansUiState.Content(summaries)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PlansUiState.Loading,
        )

    private val _deleteDialogState =
        MutableStateFlow<DeletePlanDialogState>(DeletePlanDialogState.Hidden)
    val deleteDialogState: StateFlow<DeletePlanDialogState> = _deleteDialogState.asStateFlow()

    private val _snackbarEvents = MutableSharedFlow<String>()
    val snackbarEvents: SharedFlow<String> = _snackbarEvents.asSharedFlow()

    fun requestDelete(planId: Long, planName: String) {
        _deleteDialogState.value = DeletePlanDialogState.Visible(planId, planName)
    }

    fun cancelDelete() {
        _deleteDialogState.value = DeletePlanDialogState.Hidden
    }

    fun confirmDelete() {
        val current = _deleteDialogState.value as? DeletePlanDialogState.Visible ?: return
        viewModelScope.launch {
            val result = repository.delete(current.planId)
            _deleteDialogState.value = DeletePlanDialogState.Hidden
            if (result is DeletePlanResult.Success) {
                _snackbarEvents.emit("Usunięto")
            }
        }
    }
}