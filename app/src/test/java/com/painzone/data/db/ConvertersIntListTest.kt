package com.painzone.data.db

import org.junit.Assert.assertEquals
import org.junit.Test

class ConvertersIntListTest {

    @Test
    fun `intListToCsv joins with comma`() {
        assertEquals("10,9,8", Converters.intListToCsv(listOf(10, 9, 8)))
    }

    @Test
    fun `intListToCsv single element has no separator`() {
        assertEquals("5", Converters.intListToCsv(listOf(5)))
    }

    @Test
    fun `intListToCsv null roundtrips to null`() {
        assertEquals(null, Converters.intListToCsv(null))
        assertEquals(null, Converters.csvToIntList(null))
    }

    @Test
    fun `csvToIntList parses comma-separated`() {
        assertEquals(listOf(12, 10, 8, 6), Converters.csvToIntList("12,10,8,6"))
    }

    @Test
    fun `empty string roundtrips to empty list`() {
        assertEquals(emptyList<Int>(), Converters.csvToIntList(""))
        assertEquals("", Converters.intListToCsv(emptyList()))
    }

    @Test
    fun `roundtrip preserves list`() {
        val original = listOf(8, 8, 8, 8, 8)

        val roundtripped = Converters.csvToIntList(Converters.intListToCsv(original))

        assertEquals(original, roundtripped)
    }
}