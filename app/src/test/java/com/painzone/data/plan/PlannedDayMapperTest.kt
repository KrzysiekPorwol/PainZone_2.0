package com.painzone.data.plan

import com.painzone.domain.plan.PlannedDay
import org.junit.Assert.assertEquals
import org.junit.Test

class PlannedDayMapperTest {

    @Test
    fun `entity to domain preserves all fields`() {
        val entity = PlannedDayEntity(id = 9L, trainingPlanId = 3L, name = "Push", order = 0)

        val domain = entity.toDomain()

        assertEquals(9L, domain.id)
        assertEquals(3L, domain.trainingPlanId)
        assertEquals("Push", domain.name)
        assertEquals(0, domain.order)
    }

    @Test
    fun `domain to entity preserves id=0 for new day`() {
        val domain = PlannedDay.create(trainingPlanId = 7L, name = "Pull", order = 1)

        val entity = domain.toEntity()

        assertEquals(0L, entity.id)
        assertEquals(7L, entity.trainingPlanId)
        assertEquals("Pull", entity.name)
        assertEquals(1, entity.order)
    }

    @Test
    fun `roundtrip is identity for arbitrary order`() {
        val original = PlannedDayEntity(id = 4L, trainingPlanId = 2L, name = "Legs", order = 5)

        assertEquals(original, original.toDomain().toEntity())
    }
}