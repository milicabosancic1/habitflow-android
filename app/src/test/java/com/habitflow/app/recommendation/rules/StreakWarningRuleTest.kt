package com.habitflow.app.recommendation.rules

import com.habitflow.app.data.local.HabitEntity
import com.habitflow.app.data.local.HabitEntryEntity
import com.habitflow.app.domain.DateUtils
import com.habitflow.app.domain.EntryStatus
import com.habitflow.app.domain.FrequencyType
import com.habitflow.app.domain.HabitType
import com.habitflow.app.recommendation.RecommendationContext
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StreakWarningRuleTest {

    private val today = LocalDate.of(2026, 7, 26)
    private val rule = StreakWarningRule()

    private fun habit(id: String) = HabitEntity(
        id = id,
        userId = "user",
        name = "Trčanje",
        category = "Zdravlje",
        type = HabitType.BUILD,
        frequencyType = FrequencyType.DAILY,
        createdAt = 0L,
        updatedAt = 0L
    )

    private fun entry(habitId: String, date: LocalDate, status: EntryStatus) = HabitEntryEntity(
        id = "$habitId-$date",
        habitId = habitId,
        date = DateUtils.format(date),
        status = status,
        updatedAt = 0L
    )

    @Test
    fun `prijavljuje upozorenje kad je juce propusteno a preksinoc uradjeno`() {
        val h = habit("h1")
        val entries = listOf(
            entry("h1", today.minusDays(2), EntryStatus.DONE),
            entry("h1", today.minusDays(1), EntryStatus.MISSED)
        )
        val context = RecommendationContext(
            userId = "user", today = today, habits = listOf(h), entriesByHabit = mapOf("h1" to entries)
        )

        val result = rule.evaluate(context)

        assertEquals(1, result.size)
        assertEquals("h1", result.first().habitId)
    }

    @Test
    fun `ne prijavljuje kad je juce uradjeno`() {
        val h = habit("h1")
        val entries = listOf(
            entry("h1", today.minusDays(2), EntryStatus.DONE),
            entry("h1", today.minusDays(1), EntryStatus.DONE)
        )
        val context = RecommendationContext(
            userId = "user", today = today, habits = listOf(h), entriesByHabit = mapOf("h1" to entries)
        )

        assertTrue(rule.evaluate(context).isEmpty())
    }

    @Test
    fun `ne prijavljuje ako ni preksinoc nije bilo uradjeno`() {
        val h = habit("h1")
        val entries = listOf(entry("h1", today.minusDays(1), EntryStatus.MISSED))
        val context = RecommendationContext(
            userId = "user", today = today, habits = listOf(h), entriesByHabit = mapOf("h1" to entries)
        )

        assertTrue(rule.evaluate(context).isEmpty())
    }
}
