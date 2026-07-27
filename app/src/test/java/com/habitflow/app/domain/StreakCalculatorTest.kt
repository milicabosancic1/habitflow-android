package com.habitflow.app.domain

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class StreakCalculatorTest {

    private val today = LocalDate.of(2026, 7, 26)

    @Test
    fun `currentStreak vraca 0 za praznu istoriju`() {
        assertEquals(0, StreakCalculator.currentStreak(emptySet(), today))
    }

    @Test
    fun `currentStreak broji uzastopne dane zakljucno sa danas`() {
        val doneDates = setOf("2026-07-24", "2026-07-25", "2026-07-26")
        assertEquals(3, StreakCalculator.currentStreak(doneDates, today))
    }

    @Test
    fun `currentStreak vazi i ako danasnji dan jos nije uradjen ali juce jeste`() {
        val doneDates = setOf("2026-07-24", "2026-07-25")
        assertEquals(2, StreakCalculator.currentStreak(doneDates, today))
    }

    @Test
    fun `currentStreak je 0 kad je niz prekinut pre juce`() {
        val doneDates = setOf("2026-07-20", "2026-07-21")
        assertEquals(0, StreakCalculator.currentStreak(doneDates, today))
    }

    @Test
    fun `currentStreak ignorise dane posle praznine`() {
        // 22. i 23. jul su uradjeni, ali 24. je preskocen - streak treba da se prekine tu.
        val doneDates = setOf("2026-07-22", "2026-07-23", "2026-07-25", "2026-07-26")
        assertEquals(2, StreakCalculator.currentStreak(doneDates, today))
    }

    @Test
    fun `longestStreak vraca 0 za praznu istoriju`() {
        assertEquals(0, StreakCalculator.longestStreak(emptySet()))
    }

    @Test
    fun `longestStreak pronalazi najduzi uzastopni niz bez obzira na trenutni`() {
        val doneDates = setOf(
            "2026-07-01", "2026-07-02", "2026-07-03", "2026-07-04", "2026-07-05",
            "2026-07-20"
        )
        assertEquals(5, StreakCalculator.longestStreak(doneDates))
    }

    @Test
    fun `longestStreak za jedan datum je 1`() {
        assertEquals(1, StreakCalculator.longestStreak(setOf("2026-07-26")))
    }
}
