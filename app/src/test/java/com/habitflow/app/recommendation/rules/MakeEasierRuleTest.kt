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

class MakeEasierRuleTest {

    private val today = LocalDate.of(2026, 7, 26)
    private val rule = MakeEasierRule()

    private fun habit(id: String) = HabitEntity(
        id = id,
        userId = "user",
        name = "Meditacija",
        category = "Zdravlje",
        type = HabitType.BUILD,
        frequencyType = FrequencyType.DAILY,
        createdAt = 0L,
        updatedAt = 0L
    )

    private fun missed(habitId: String, daysAgo: Long) = HabitEntryEntity(
        id = "$habitId-$daysAgo",
        habitId = habitId,
        date = DateUtils.format(today.minusDays(daysAgo)),
        status = EntryStatus.MISSED,
        updatedAt = 0L
    )

    @Test
    fun `prijavljuje kad je navika propustena tri ili vise puta ove nedelje`() {
        val h = habit("h1")
        val entries = listOf(missed("h1", 1), missed("h1", 2), missed("h1", 3))
        val context = RecommendationContext(
            userId = "user", today = today, habits = listOf(h), entriesByHabit = mapOf("h1" to entries)
        )

        val result = rule.evaluate(context)

        assertEquals(1, result.size)
        assertEquals("h1", result.first().habitId)
    }

    @Test
    fun `ne prijavljuje ispod praga od tri propusta`() {
        val h = habit("h1")
        val entries = listOf(missed("h1", 1), missed("h1", 2))
        val context = RecommendationContext(
            userId = "user", today = today, habits = listOf(h), entriesByHabit = mapOf("h1" to entries)
        )

        assertTrue(rule.evaluate(context).isEmpty())
    }

    @Test
    fun `ne racuna propuste van sedmodnevnog prozora`() {
        val h = habit("h1")
        val entries = listOf(missed("h1", 1), missed("h1", 2), missed("h1", 20))
        val context = RecommendationContext(
            userId = "user", today = today, habits = listOf(h), entriesByHabit = mapOf("h1" to entries)
        )

        assertTrue(rule.evaluate(context).isEmpty())
    }
}
