package com.painzone.domain.plan

import java.time.Instant

data class TrainingPlan(
    val id: Long,
    val name: String,
    val isActive: Boolean,
    val createdAt: Instant,
    val icon: PlanIcon = PlanIcon.DEFAULT,
) {
    init {
        require(name == name.trim()) { "name must be trimmed" }
        require(name.isNotEmpty()) { "name must be non-blank" }
    }

    fun rename(newName: String): TrainingPlan = copy(name = newName.trim())

    fun activate(): TrainingPlan = copy(isActive = true)

    fun deactivate(): TrainingPlan = copy(isActive = false)

    companion object {
        fun create(name: String, now: Instant, icon: PlanIcon = PlanIcon.DEFAULT): TrainingPlan =
            TrainingPlan(
                id = 0L,
                name = name.trim(),
                isActive = false,
                createdAt = now,
                icon = icon,
            )
    }
}