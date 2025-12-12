package com.example.dataagrin.app.presentation.ui.utils

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TimeValidationTest {

    // ==================== isValidTimeFormat Tests ====================

    @Test
    fun `isValidTimeFormat returns true for valid time 00-00`() {
        assertTrue(TimeValidation.isValidTimeFormat("00:00"))
    }

    @Test
    fun `isValidTimeFormat returns true for valid time 23-59`() {
        assertTrue(TimeValidation.isValidTimeFormat("23:59"))
    }

    @Test
    fun `isValidTimeFormat returns true for valid time 12-30`() {
        assertTrue(TimeValidation.isValidTimeFormat("12:30"))
    }

    @Test
    fun `isValidTimeFormat returns true for single digit hour`() {
        assertTrue(TimeValidation.isValidTimeFormat("9:30"))
    }

    @Test
    fun `isValidTimeFormat returns false for empty string`() {
        assertFalse(TimeValidation.isValidTimeFormat(""))
    }

    @Test
    fun `isValidTimeFormat returns false for invalid hour 24`() {
        assertFalse(TimeValidation.isValidTimeFormat("24:00"))
    }

    @Test
    fun `isValidTimeFormat returns false for invalid minute 60`() {
        assertFalse(TimeValidation.isValidTimeFormat("12:60"))
    }

    @Test
    fun `isValidTimeFormat returns false for malformed time`() {
        assertFalse(TimeValidation.isValidTimeFormat("12-30"))
        assertFalse(TimeValidation.isValidTimeFormat("1230"))
        assertFalse(TimeValidation.isValidTimeFormat("12:3"))
    }

    @Test
    fun `isValidTimeFormat returns false for negative values`() {
        assertFalse(TimeValidation.isValidTimeFormat("-1:00"))
    }

    // ==================== isValidTimeRange Tests ====================

    @Test
    fun `isValidTimeRange returns true for valid time in range`() {
        assertTrue(TimeValidation.isValidTimeRange("12:30"))
        assertTrue(TimeValidation.isValidTimeRange("00:00"))
        assertTrue(TimeValidation.isValidTimeRange("23:59"))
    }

    @Test
    fun `isValidTimeRange returns false for invalid format`() {
        assertFalse(TimeValidation.isValidTimeRange("25:00"))
        assertFalse(TimeValidation.isValidTimeRange("12:61"))
    }

    @Test
    fun `isValidTimeRange returns false for empty string`() {
        assertFalse(TimeValidation.isValidTimeRange(""))
    }

    // ==================== isEndTimeAfterStartTime Tests ====================

    @Test
    fun `isEndTimeAfterStartTime returns true when end is after start`() {
        assertTrue(TimeValidation.isEndTimeAfterStartTime("08:00", "10:00"))
        assertTrue(TimeValidation.isEndTimeAfterStartTime("08:00", "08:01"))
        assertTrue(TimeValidation.isEndTimeAfterStartTime("00:00", "23:59"))
    }

    @Test
    fun `isEndTimeAfterStartTime returns false when end equals start`() {
        assertFalse(TimeValidation.isEndTimeAfterStartTime("10:00", "10:00"))
    }

    @Test
    fun `isEndTimeAfterStartTime returns false when end is before start`() {
        assertFalse(TimeValidation.isEndTimeAfterStartTime("10:00", "08:00"))
        assertFalse(TimeValidation.isEndTimeAfterStartTime("23:59", "00:00"))
    }

    @Test
    fun `isEndTimeAfterStartTime returns false for invalid start time`() {
        assertFalse(TimeValidation.isEndTimeAfterStartTime("25:00", "10:00"))
        assertFalse(TimeValidation.isEndTimeAfterStartTime("", "10:00"))
    }

    @Test
    fun `isEndTimeAfterStartTime returns false for invalid end time`() {
        assertFalse(TimeValidation.isEndTimeAfterStartTime("08:00", "25:00"))
        assertFalse(TimeValidation.isEndTimeAfterStartTime("08:00", ""))
    }

    @Test
    fun `isEndTimeAfterStartTime handles minute boundary correctly`() {
        assertTrue(TimeValidation.isEndTimeAfterStartTime("08:59", "09:00"))
        assertFalse(TimeValidation.isEndTimeAfterStartTime("09:00", "08:59"))
    }
}
