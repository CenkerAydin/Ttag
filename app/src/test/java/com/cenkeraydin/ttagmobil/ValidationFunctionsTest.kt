package com.cenkeraydin.ttagmobil

import com.cenkeraydin.ttagmobil.util.createDateTime
import com.cenkeraydin.ttagmobil.util.isPasswordValid
import com.cenkeraydin.ttagmobil.util.isValidDate
import com.cenkeraydin.ttagmobil.util.isValidPersonCount
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class ValidationFunctionsTest {

    @Test
    fun `isValidDate should return true for valid date format`() {
        assertTrue(isValidDate("2023-10-05"))
        assertTrue(isValidDate("2023-01-01"))
        assertTrue(isValidDate("2023-12-31"))
    }


    @Test
    fun `isValidPersonCount should return true for valid positive integer`() {
        assertTrue(isValidPersonCount("1"))
        assertTrue(isValidPersonCount("42"))
    }

    @Test
    fun `isValidPersonCount should return false for invalid or non-positive input`() {
        assertFalse(isValidPersonCount("0"))      // Zero
        assertFalse(isValidPersonCount("-1"))     // Negative
        assertFalse(isValidPersonCount("abc"))    // Non-numeric
        assertFalse(isValidPersonCount(""))       // Empty string
        assertFalse(isValidPersonCount("1.5"))    // Decimal
    }
    
    @Test
    fun `createDateTime should return LocalDateTime for valid date and time`() {
        val result = createDateTime("2025-05-17", "14:30")
        val expected = LocalDateTime.parse("2025-05-17 14:30", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        assertEquals(expected, result)
    }

    @Test
    fun `isPasswordValid should return true for valid password`() {
        assertTrue(isPasswordValid("Ab1@def"))      // Meets all criteria
        assertTrue(isPasswordValid("P@ssw0rd123"))  // Longer valid password
    }

    @Test
    fun `isPasswordValid should return false for invalid password`() {
        assertFalse(isPasswordValid("abcdef"))      // No uppercase, digit, or special char
        assertFalse(isPasswordValid("ABCDEF"))      // No lowercase, digit, or special char
        assertFalse(isPasswordValid("Abcdef"))      // No digit or special char
        assertFalse(isPasswordValid("Ab1def"))      // No special char
        assertFalse(isPasswordValid("Ab@12"))       // Too short
        assertFalse(isPasswordValid(""))            // Empty string
    }

}