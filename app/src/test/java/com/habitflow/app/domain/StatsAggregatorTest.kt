package com.habitflow.app.domain

import com.habitflow.app.data.local.HabitEntity
import com.habitflow.app.data.local.HabitEntryEntity
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class StatsAggregatorTest {

    private val today = LocalDate.of(2026, 7, 26)

    private fun habit(id: String, category: String = "Opšte") = HabitEntity(
        id = id,
        userId = "user",
        name = "Navika $id",
        category = category,
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
    fun `completionPct je 0 kad nema navika`() {
        val pct = StatsAggregator.completionPct(emptyList(), emptyMap(), today.minusDays(6), today)
        assertEquals(0, pct)
    }

    @Test
    fun `completionPct racuna procenat zavrsenih dana u opsegu`() {
        val h = habit("h1")
        val entries = listOf(
            entry("h1", today, EntryStatus.DONE),
            entry("h1", today.minusDays(1), EntryStatus.DONE),
            entry("h1", today.minusDays(2), EntryStatus.MISSED)
        )
        // Opseg od 4 dana (today-3..today), samo 2 od 4 mogућа dana su DONE = 50%.
        val pct = StatsAggregator.completionPct(
            listOf(h),
            mapOf("h1" to entries),
            today.minusDays(3),
            today
        )
        assertEquals(50, pct)
    }

    @Test
    fun `completionPct ignorise unose van opsega`() {
        val h = habit("h1")
        val entries = listOf(entry("h1", today.minusDays(10), EntryStatus.DONE))
        val pct = StatsAggregator.completionPct(
            listOf(h),
            mapOf("h1" to entries),
            today.minusDays(3),
            today
        )
        assertEquals(0, pct)
    }

    @Test
    fun `trendDeltaPct poredi ovu i proslu nedelju`() {
        val h = habit("h1")
        val entries = (0..6).map { entry("h1", today.minusDays(it.toLong()), EntryStatus.DONE)
        }
        // Sve ove nedelje uradjeno (100%), prosle nedelje nista (0%) -> delta +100.
        val delta = StatsAggregator.trendDeltaPct(listOf(h), mapOf("h1" to entries), today)
        assertEquals(100, delta)
    }

    @Test
    fun `categoryStats grupise po kategoriji`() {
        val h1 = habit("h1", category = "Zdravlje")
        val h2 = habit("h2", category = "Učenje")
        val entries = mapOf(
            "h1" to listOf(entry("h1", today, EntryStatus.DONE)),
            "h2" to emptyList()
        )
        val stats = StatsAggregator.categoryStats(listOf(h1, h2), entries, today)
        assertEquals(2, stats.size)
        assertEquals(setOf("Zdravlje", "Učenje"), stats.map { it.category }.toSet())
    }
}
