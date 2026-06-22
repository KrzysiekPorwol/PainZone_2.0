package com.painzone.ui.session

import com.painzone.domain.session.Rpe
import org.junit.Assert.assertEquals
import org.junit.Test

class LastSetPreviewFormatTest {

    @Test
    fun `daysAgoLabel reads naturally in Polish`() {
        assertEquals("dziś", daysAgoLabel(0))
        assertEquals("dziś", daysAgoLabel(-1)) // future/clock skew clamps to today
        assertEquals("wczoraj", daysAgoLabel(1))
        assertEquals("3 dni temu", daysAgoLabel(3))
    }

    @Test
    fun `preview line includes RPE when recorded`() {
        val line = lastSetPreviewLine(LastSetPreviewUi(reps = 10, weight = 60.0, rpe = Rpe.Hard, daysAgo = 3))
        assertEquals("10 × 60 kg / Ciężka — 3 dni temu", line)
    }

    @Test
    fun `preview line drops RPE suffix when absent and keeps half-step weight`() {
        val line = lastSetPreviewLine(LastSetPreviewUi(reps = 8, weight = 62.5, rpe = null, daysAgo = 1))
        assertEquals("8 × 62.5 kg — wczoraj", line)
    }
}
