package com.painzone.domain.stats

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.Instant
import java.time.temporal.ChronoUnit

class StatsPeriodTest {

    private val now = Instant.parse("2026-06-24T12:00:00Z")

    @Test
    fun `since subtracts the window in days`() {
        assertEquals(now.minus(30, ChronoUnit.DAYS), StatsPeriod.LAST_30_DAYS.since(now))
        assertEquals(now.minus(90, ChronoUnit.DAYS), StatsPeriod.LAST_90_DAYS.since(now))
        assertEquals(now.minus(365, ChronoUnit.DAYS), StatsPeriod.LAST_YEAR.since(now))
    }

    @Test
    fun `ALL has no lower bound`() {
        assertNull(StatsPeriod.ALL.days)
        assertNull(StatsPeriod.ALL.since(now))
    }

    @Test
    fun `default period is 90 days per S10`() {
        assertEquals(StatsPeriod.LAST_90_DAYS, StatsPeriod.DEFAULT)
    }
}
