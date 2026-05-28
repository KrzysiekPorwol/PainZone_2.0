package com.painzone.domain.plan

data class PlannedDay(
    val id: Long,
    val trainingPlanId: Long,
    val name: String,
    val order: Int,
) {
    init {
        require(name == name.trim()) { "name must be trimmed" }
        require(name.isNotEmpty()) { "name must be non-blank" }
        require(order >= 0) { "order must be >= 0" }
    }

    fun rename(newName: String): PlannedDay = copy(name = newName.trim())

    fun reorder(newOrder: Int): PlannedDay = copy(order = newOrder)

    companion object {
        fun create(trainingPlanId: Long, name: String, order: Int): PlannedDay =
            PlannedDay(
                id = 0L,
                trainingPlanId = trainingPlanId,
                name = name.trim(),
                order = order,
            )
    }
}