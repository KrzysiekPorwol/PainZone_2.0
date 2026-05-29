package com.painzone.ui.plans

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.painzone.domain.plan.DeletePlanResult
import com.painzone.domain.plan.PlanRepository
import com.painzone.domain.plan.PlanSummary
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

    private val _confirmState =
        MutableStateFlow<ActivationConfirmState>(ActivationConfirmState.Hidden)
    val confirmState: StateFlow<ActivationConfirmState> = _confirmState.asStateFlow()

    private val _snackbarEvents = MutableSharedFlow<String>()
    val snackbarEvents: SharedFlow<String> = _snackbarEvents.asSharedFlow()

    fun onToggleActive(plan: PlanSummary) {
        val otherActive = (uiState.value as? PlansUiState.Content)?.items
            ?.firstOrNull { it.isActive && it.id != plan.id }
        when (val decision = activationDecision(plan.isActive, otherActive?.name)) {
            ActivationDecision.Activate -> activate(plan.id)
            ActivationDecision.Deactivate -> deactivate(plan.id)
            is ActivationDecision.Confirm ->
                _confirmState.value = ActivationConfirmState.Visible(
                    planId = plan.id,
                    planName = plan.name,
                    currentActiveName = decision.currentActiveName,
                )
        }
    }

    fun confirmActivation() {
        val current = _confirmState.value as? ActivationConfirmState.Visible ?: return
        _confirmState.value = ActivationConfirmState.Hidden
        activate(current.planId)
    }

    fun cancelActivation() {
        _confirmState.value = ActivationConfirmState.Hidden
    }

    private fun activate(planId: Long) = viewModelScope.launch {
        repository.setActive(planId)
        _snackbarEvents.emit("Plan aktywowany")
    }

    private fun deactivate(planId: Long) = viewModelScope.launch {
        repository.deactivate(planId)
        _snackbarEvents.emit("Plan odznaczony")
    }

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