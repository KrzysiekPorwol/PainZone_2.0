package com.painzone.ui.plans

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.painzone.domain.plan.CreatePlanResult
import com.painzone.domain.plan.PlanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class PlanCreateViewModel @Inject constructor(
    private val repository: PlanRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlanCreateUiState())
    val uiState: StateFlow<PlanCreateUiState> = _uiState.asStateFlow()

    // One-shot signal: plan persisted, screen should pop back to Plans.
    private val _savedEvents = MutableSharedFlow<Unit>()
    val savedEvents: SharedFlow<Unit> = _savedEvents.asSharedFlow()

    fun onNameChange(value: String) {
        _uiState.update { it.copy(name = value, nameError = null) }
    }

    // Session count is stepper-driven (1..7). Grow appends a blank session;
    // shrink drops the last session. List index becomes the persisted order.
    fun incrementDays() {
        _uiState.update { state ->
            if (state.days.size >= PlanCreateUiState.MAX_DAY_COUNT) return@update state
            state.copy(days = state.days + "")
        }
    }

    fun decrementDays() {
        _uiState.update { state ->
            if (state.days.size <= PlanCreateUiState.MIN_DAY_COUNT) return@update state
            state.copy(days = state.days.dropLast(1))
        }
    }

    fun onDayNameChange(index: Int, value: String) {
        _uiState.update { state ->
            state.copy(days = state.days.mapIndexed { i, name -> if (i == index) value else name })
        }
    }

    fun save() {
        val state = _uiState.value
        if (!state.canSave) return
        _uiState.update { it.copy(saving = true) }
        viewModelScope.launch {
            when (val result = repository.create(state.name)) {
                is CreatePlanResult.Success -> {
                    // Days were validated unique locally — addDay won't hit DuplicateName/PlanNotFound.
                    state.days.forEach { dayName -> repository.addDay(result.id, dayName.trim()) }
                    _savedEvents.emit(Unit)
                }
                CreatePlanResult.DuplicateName -> {
                    _uiState.update {
                        it.copy(saving = false, nameError = "Plan o tej nazwie już istnieje")
                    }
                }
            }
        }
    }
}