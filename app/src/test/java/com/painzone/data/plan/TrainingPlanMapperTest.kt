package com.painzone.data.plan

import com.painzone.domain.plan.PlanIcon
import com.painzone.domain.plan.TrainingPlan
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

class TrainingPlanMapperTest {

    private val t0: Instant = Instant.parse("2026-01-01T10:00:00Z")

    @Test
    fun `entity to domain preserves all fields when inactive`() {
        val entity = TrainingPlanEntity(
            id = 5L,
            name = "PPL",
            isActive = false,
            createdAt = t0,
        )

        val domain = entity.toDomain()

        assertEquals(5L, domain.id)
        assertEquals("PPL", domain.name)
        assertEquals(false, domain.isActive)
        assertEquals(t0, domain.createdAt)
    }

    @Test
    fun `entity to domain preserves isActive=true`() {
        val entity = TrainingPlanEntity(id = 1L, name = "FBW", isActive = true, createdAt = t0)

        assertEquals(true, entity.toDomain().isActive)
    }

    @Test
    fun `domain to entity preserves id=0 for new plan`() {
        val domain = TrainingPlan.create(name = "Upper/Lower", now = t0)

        val entity = domain.toEntity()

        assertEquals(0L, entity.id)
        assertEquals("Upper/Lower", entity.name)
        assertEquals(false, entity.isActive)
        assertEquals(t0, entity.createdAt)
    }

    @Test
    fun `roundtrip entity to domain to entity is identity`() {
        val original = TrainingPlanEntity(id = 42L, name = "Split", isActive = true, createdAt = t0)

        assertEquals(original, original.toDomain().toEntity())
    }

    @Test
    fun `icon round-trips by enum name`() {
        val entity = TrainingPlanEntity(
            id = 7L,
            name = "Cardio",
            isActive = false,
            createdAt = t0,
            icon = "DIRECTIONS_RUN",
        )

        val domain = entity.toDomain()

        assertEquals(PlanIcon.DIRECTIONS_RUN, domain.icon)
        assertEquals("DIRECTIONS_RUN", domain.toEntity().icon)
    }

    @Test
    fun `unknown icon name falls back to default`() {
        val entity = TrainingPlanEntity(
            id = 8L,
            name = "Legacy",
            isActive = false,
            createdAt = t0,
            icon = "SOMETHING_REMOVED",
        )

        assertEquals(PlanIcon.DEFAULT, entity.toDomain().icon)
    }
}