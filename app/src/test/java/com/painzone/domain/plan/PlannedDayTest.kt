package com.painzone.domain.plan

import org.junit.Assert.assertEquals
import org.junit.Test

class PlannedDayTest {

    // --- create ---------------------------------------------------------------

    @Test
    fun `create returns day with trimmed name, id=0, and given planId and order`() {
        val d = PlannedDay.create(trainingPlanId = 42L, name = "Push", order = 0)

        assertEquals(0L, d.id)
        assertEquals(42L, d.trainingPlanId)
        assertEquals("Push", d.name)
        assertEquals(0, d.order)
    }

    @Test
    fun `create trims surrounding whitespace`() {
        val d = PlannedDay.create(trainingPlanId = 1L, name = "  Pull  ", order = 1)
        assertEquals("Pull", d.name)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `create with blank name throws`() {
        PlannedDay.create(trainingPlanId = 1L, name = "", order = 0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `create with whitespace-only name throws`() {
        PlannedDay.create(trainingPlanId = 1L, name = "   ", order = 0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `create with negative order throws`() {
        PlannedDay.create(trainingPlanId = 1L, name = "Push", order = -1)
    }

    // --- init invariants (direct construction) --------------------------------

    @Test(expected = IllegalArgumentException::class)
    fun `direct construction with untrimmed name throws`() {
        PlannedDay(id = 1L, trainingPlanId = 1L, name = " Push ", order = 0)
    }

    @Test
    fun `order zero is allowed`() {
        val d = PlannedDay(id = 1L, trainingPlanId = 1L, name = "Push", order = 0)
        assertEquals(0, d.order)
    }

    // --- rename ---------------------------------------------------------------

    @Test
    fun `rename updates name and keeps id, planId, order`() {
        val d = PlannedDay.create(7L, "Push", 2).copy(id = 11L)
        val renamed = d.rename("Pull")

        assertEquals(11L, renamed.id)
        assertEquals(7L, renamed.trainingPlanId)
        assertEquals("Pull", renamed.name)
        assertEquals(2, renamed.order)
    }

    @Test
    fun `rename trims new name`() {
        val d = PlannedDay.create(1L, "Push", 0)
        assertEquals("Legs", d.rename("  Legs  ").name)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `rename to blank throws`() {
        PlannedDay.create(1L, "Push", 0).rename("   ")
    }

    // --- reorder --------------------------------------------------------------

    @Test
    fun `reorder updates order and keeps other fields`() {
        val d = PlannedDay.create(7L, "Push", 0).copy(id = 11L)
        val moved = d.reorder(3)

        assertEquals(11L, moved.id)
        assertEquals(7L, moved.trainingPlanId)
        assertEquals("Push", moved.name)
        assertEquals(3, moved.order)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `reorder to negative throws`() {
        PlannedDay.create(1L, "Push", 0).reorder(-1)
    }
}