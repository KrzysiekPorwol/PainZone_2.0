package com.painzone.domain.session

import com.painzone.domain.plan.PlannedDay
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DaySuggestionTest {

    private fun day(id: Long, name: String, order: Int) =
        PlannedDay(id = id, trainingPlanId = 1L, name = name, order = order)

    private val ppl = listOf(
        day(1L, "Push", 0),
        day(2L, "Pull", 1),
        day(3L, "Legs", 2),
    )

    @Test
    fun `no history suggests the first day by order`() {
        assertEquals(1L, suggestNextDay(ppl, lastTrainedDayId = null)!!.id)
    }

    @Test
    fun `after Push suggests Pull`() {
        assertEquals(2L, suggestNextDay(ppl, lastTrainedDayId = 1L)!!.id)
    }

    @Test
    fun `after the last day wraps around to the first`() {
        assertEquals(1L, suggestNextDay(ppl, lastTrainedDayId = 3L)!!.id)
    }

    @Test
    fun `unordered input is sorted by order before rotating`() {
        val shuffled = listOf(ppl[2], ppl[0], ppl[1])
        assertEquals(3L, suggestNextDay(shuffled, lastTrainedDayId = 2L)!!.id)
    }

    @Test
    fun `anchor day no longer in the plan falls back to the first day`() {
        // e.g. the last trained day was removed from the plan since.
        assertEquals(1L, suggestNextDay(ppl, lastTrainedDayId = 99L)!!.id)
    }

    @Test
    fun `empty plan suggests nothing`() {
        assertNull(suggestNextDay(emptyList(), lastTrainedDayId = null))
    }

    @Test
    fun `single-day plan always suggests that day`() {
        val single = listOf(day(5L, "Full Body", 0))
        assertEquals(5L, suggestNextDay(single, lastTrainedDayId = 5L)!!.id)
    }
}
