package com.assignment.navlog.utils

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class ConverterTest {
    @Test
    fun testToMinutes() {
        val duration = "00.20"
        val result = toMinutes(duration)
        assertEquals(20, result)
    }

    @Test
    fun testToISO8601Duration() {
        val duration = "01.20"
        val result = toISO8601Duration(duration)
        assertEquals("PT1H20M", result)
    }
}