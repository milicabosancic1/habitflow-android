package com.habitflow.app.domain

import com.habitflow.app.data.local.HabitEntity
import com.habitflow.app.data.local.HabitEntryEntity
import java.time.LocalDate
import java.time.YearMonth
import org.junit.Assert.assertEquals
import org.junit.Test

class MonthlyCalendarTest {

    private fun habit(id: String) = HabitEntity(
        id = id,
        userId = "user",
        name = "Navika $id",
        category = "Opšte",
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
    fun `racuna procenat po danu za sve navike`() {
        val month = YearMonth.of(2026, 8)
        val h1 = habit("h1")
        val h2 = habit("h2")
        val entries = mapOf(
            "h1" to listOf(entry("h1", month.atDay(3), EntryStatus.DONE)),
            "h2" to listOf(entry("h2", month.atDay(3), EntryStatus.DONE), entry("h2", month.atDay(5), EntryStatus.DONE))
        )

        val result = MonthlyCalendar.completionByDay(listOf(h1, h2), entries, month)

        assertEquals(1f, result[month.atDay(3)])
        assertEquals(0.5f, result[month.atDay(5)])
        assertEquals(0f, result[month.atDay(1)])
    }

    @Test
    fun `filtrirano na jednu naviku racuna samo za nju`() {
        val month = YearMonth.of(2026, 8)
        val h1 = habit("h1")
        val entries = mapOf("h1" to listOf(entry("h1", month.atDay(3), EntryStatus.DONE)))

        val result = MonthlyCalendar.completionByDay(listOf(h1), entries, month)

        assertEquals(1f, result[month.atDay(3)])
        assertEquals(0f, result[month.atDay(4)])
    }

    @Test
    fun `pokriva sve dane u mesecu`() {
        val month = YearMonth.of(2026, 2)
        val result = MonthlyCalendar.completionByDay(emptyList(), emptyMap(), month)

        assertEquals(month.lengthOfMonth(), result.size)
    }

    @Test
    fun `SPECIFIC_DAYS navika ne racuna se u imenilac dana kad nije zakazana`() {
        // 2026-08-03 je ponedeljak; h2 zakazana samo sredom (ISO dan 3), pa se ne racuna u ponedeljak.
        val month = YearMonth.of(2026, 8)
        val h1 = habit("h1") // DAILY, uradjena
        val h2 = habit("h2").copy(frequencyType = FrequencyType.SPECIFIC_DAYS, daysOfWeek = "3")
        val entries = mapOf("h1" to listOf(entry("h1", month.atDay(3), EntryStatus.DONE)))

        val result = MonthlyCalendar.completionByDay(listOf(h1, h2), entries, month)

        // Bez fix-a bi bilo 1/2=0.5 (h2 bi vukla dole iako tog dana nije ni predvidjena).
        assertEquals(1f, result[month.atDay(3)])
    }
}
