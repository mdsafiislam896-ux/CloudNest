package com.safiislam.nexvault.desktop.common

import com.safiislam.nexvault.presentation.common.Formatters
import org.junit.Assert.assertEquals
import org.junit.Test

class FormattersMilestone2Test {

    @Test
    fun formatPercent_fraction_formatsCorrectPercentage() {
        assertEquals("0%", Formatters.percent(0.0f))
        assertEquals("68%", Formatters.percent(0.684f))
        assertEquals("69%", Formatters.percent(0.686f))
        assertEquals("100%", Formatters.percent(1.0f))
    }

    @Test
    fun formatPercent_bytes_computesAndFormats() {
        assertEquals("0%", Formatters.formatPercent(0L, 1000L))
        assertEquals("50%", Formatters.formatPercent(500L, 1000L))
        assertEquals("68%", Formatters.formatPercent(680L, 1000L))
        assertEquals("100%", Formatters.formatPercent(1200L, 1000L))
        assertEquals("0%", Formatters.formatPercent(500L, 0L))
    }

    @Test
    fun bytes_formatsStandardMagnitudes() {
        assertEquals("0 B", Formatters.bytes(0L))
        assertEquals("500 B", Formatters.bytes(500L))
        assertEquals("1.0 KB", Formatters.bytes(1024L))
        assertEquals("289.0 GB", Formatters.bytes(289L * 1024 * 1024 * 1024))
        assertEquals("785.0 GB", Formatters.bytes(785L * 1024 * 1024 * 1024))
    }
}
