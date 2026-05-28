package com.painzone.domain.plan

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class TrainingPlanTest {

    private val t0: Instant = Instant.parse("2026-01-01T10:00:00Z")

    // --- create ---------------------------------------------------------------

    @Test
    fun `create returns inactive plan with trimmed name and id=0`() {
        val p = TrainingPlan.create(name = "Push-Pull-Legs", now = t0)

        assertEquals(0L, p.id)
        assertEquals("Push-Pull-Legs", p.name)
        assertFalse(p.isActive)
        assertEquals(t0, p.createdAt)
    }

    @Test
    fun `create trims surrounding whitespace`() {
        val p = TrainingPlan.create(name = "  PPL  ", now = t0)
        assertEquals("PPL", p.name)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `create with blank name throws`() {
        TrainingPlan.create(name = "", now = t0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `create with whitespace-only name throws`() {
        TrainingPlan.create(name = "   ", now = t0)
    }

    // --- init invariants (direct construction) --------------------------------

    @Test(expected = IllegalArgumentException::class)
    fun `direct construction with untrimmed name throws`() {
        TrainingPlan(id = 1L, name = " PPL ", isActive = false, createdAt = t0)
    }

    // --- rename ---------------------------------------------------------------

    @Test
    fun `rename updates name and keeps id, isActive and createdAt`() {
        val p = TrainingPlan.create("PPL", t0).copy(id = 5L, isActive = true)
        val renamed = p.rename("Upper-Lower")

        assertEquals(5L, renamed.id)
        assertEquals("Upper-Lower", renamed.name)
        assertTrue(renamed.isActive)
        assertEquals(t0, renamed.createdAt)
    }

    @Test
    fun `rename trims new name`() {
        val p = TrainingPlan.create("PPL", t0)
        assertEquals("Upper-Lower", p.rename("  Upper-Lower  ").name)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `rename to blank throws`() {
        TrainingPlan.create("PPL", t0).rename("   ")
    }

    // --- activate / deactivate ------------------------------------------------

    @Test
    fun `activate flips isActive to true and keeps other fields`() {
        val p = TrainingPlan.create("PPL", t0).copy(id = 3L)
        val active = p.activate()

        assertEquals(3L, active.id)
        assertEquals("PPL", active.name)
        assertEquals(t0, active.createdAt)
        assertTrue(active.isActive)
    }

    @Test
    fun `deactivate flips isActive to false and keeps other fields`() {
        val p = TrainingPlan.create("PPL", t0).copy(id = 3L, isActive = true)
        val inactive = p.deactivate()

        assertEquals(3L, inactive.id)
        assertEquals("PPL", inactive.name)
        assertEquals(t0, inactive.createdAt)
        assertFalse(inactive.isActive)
    }
}