package com.painzone.domain.session

import com.painzone.domain.plan.PlannedDay

// Smart suggestion (S1): the day to train next within the active plan. Rotates to the day right
// after the most recently trained one (wrap-around modulo the plan's days), so Push → Pull → Legs
// → Push. No history (or the anchor day no longer exists) → the first day by order.
// See docs/05-domain-session.md#Smart-suggestion.
fun suggestNextDay(days: List<PlannedDay>, lastTrainedDayId: Long?): PlannedDay? {
    val ordered = days.sortedBy { it.order }
    if (ordered.isEmpty()) return null
    val anchorIndex = lastTrainedDayId?.let { id -> ordered.indexOfFirst { it.id == id } } ?: -1
    if (anchorIndex < 0) return ordered.first()
    return ordered[(anchorIndex + 1) % ordered.size]
}
