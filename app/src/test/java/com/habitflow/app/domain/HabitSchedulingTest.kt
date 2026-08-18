package com.habitflow.app.domain

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HabitSchedulingTest {

    // 2026-07-27 je ponedeljak.
    private val monday = LocalDate.of(2026, 7, 27)
    private val wednesday = monday.plusDays(2)
    private val sunday = monday.plusDays(6)

    @Test
    fun `parseDays cita CSV ISO brojeve dana`() {
        assertEquals(setOf(1, 3, 5), HabitScheduling.parseDays("1,3,5"))
    }

    @Test
    fun `parseDays vraca prazan skup za null ili prazan string`() {
        assertEquals(emptySet<Int>(), HabitScheduling.parseDays(null))
        assertEquals(emptySet<Int>(), HabitScheduling.parseDays(""))
    }

    @Test
    fun `formatDays sortira i spaja zarezom`() {
        assertEquals("1,3,5", HabitScheduling.formatDays(setOf(5, 1, 3)))
    }

    @Test
    fun `DAILY je zakazano svakog dana`() {
        assertTrue(HabitScheduling.isScheduledOn(FrequencyType.DAILY, null, sunday))
    }

    @Test
    fun `TIMES_PER_WEEK je zakazano svakog dana`() {
        assertTrue(HabitScheduling.isScheduledOn(FrequencyType.TIMES_PER_WEEK, null, sunday))
    }

    @Test
    fun `SPECIFIC_DAYS je zakazano samo na izabrane dane`() {
        val days = "1,3" // ponedeljak, sreda
        assertTrue(HabitScheduling.isScheduledOn(FrequencyType.SPECIFIC_DAYS, days, monday))
        assertTrue(HabitScheduling.isScheduledOn(FrequencyType.SPECIFIC_DAYS, days, wednesday))
        assertEquals(false, HabitScheduling.isScheduledOn(FrequencyType.SPECIFIC_DAYS, days, sunday))
    }

    @Test
    fun `SPECIFIC_DAYS bez izabranih dana nije zakazano nigde`() {
        assertEquals(false, HabitScheduling.isScheduledOn(FrequencyType.SPECIFIC_DAYS, null, monday))
    }
}
