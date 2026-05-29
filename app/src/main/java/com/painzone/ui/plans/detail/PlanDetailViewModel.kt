package com.painzone.ui.plans.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.painzone.domain.plan.PlanRepository
import com.painzone.domain.plan.TrainingPlan
import com.painzone.ui.navigation.PlanDetail
import com.painzone.ui.plans.ActivationConfirmState
import com.painzone.ui.plans.ActivationDecision
import com.painzone.ui.plans.activationDecision
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
class PlanDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: PlanRepository,
) : ViewModel() {

    private val planId: Long = savedStateHandle.toRoute<PlanDetail>().planId

    // Cached so onToggleActive can name the plan it would replace without re-querying.
    // Eagerly: read only via .value (never collected by the UI), so the upstream must
    // start without waiting for a subscriber — otherwise .value would stay null.
    private val activePlan: StateFlow<TrainingPlan?> =
        repository.observeActive()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = null,
            )

    val uiState: StateFlow<PlanDetailUiState> =
        repository.observePlanWithDays(planId)
            .map { planWithDays ->
                if (planWithDays == null) {
                    PlanDetailUiState.NotFound
                } else {
                    PlanDetailUiState.Content(
                        planName = planWithDays.plan.name,
                        isActive = planWithDays.plan.isActive,
                        days = planWithDays.days.map { it.day },
                    )
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = PlanDetailUiState.Loading,
            )

    private val _confirmState =
        MutableStateFlow<ActivationConfirmState>(ActivationConfirmState.Hidden)
    val confirmState: StateFlow<ActivationConfirmState> = _confirmState.asStateFlow()

    private val _snackbarEvents = MutableSharedFlow<String>()
    val snackbarEvents: SharedFlow<String> = _snackbarEvents.asSharedFlow()

    fun onToggleActive() {
        val content = uiState.value as? PlanDetailUiState.Content ?: return
        val otherActiveName = activePlan.value?.takeIf { it.id != planId }?.name
        when (val decision = activationDecision(content.isActive, otherActiveName)) {
            ActivationDecision.Activate -> activate()
            ActivationDecision.Deactivate -> deactivate()
            is ActivationDecision.Confirm ->
                _confirmState.value = ActivationConfirmState.Visible(
                    planId = planId,
                    planName = content.planName,
                    currentActiveName = decision.currentActiveName,
                )
        }
    }

    fun confirmActivation() {
        _confirmState.value = ActivationConfirmState.Hidden
        activate()
    }

    fun cancelActivation() {
        _confirmState.value = ActivationConfirmState.Hidden
    }

    private fun activate() = viewModelScope.launch {
        repository.setActive(planId)
        _snackbarEvents.emit("Plan aktywowany")
    }

    private fun deactivate() = viewModelScope.launch {
        repository.deactivate(planId)
        _snackbarEvents.emit("Plan odznaczony")
    }
}