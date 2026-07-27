package com.habitflow.app.domain

import java.time.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Test

class ReminderSchedulingTest {

    @Test
    fun `delayUntilNext racuna kasnjenje do danasnjeg vremena ako jos nije proslo`() {
        val now = LocalDateTime.of(2026, 7, 26, 8, 0)
        val delay = ReminderScheduling.delayUntilNext("09:00", now)
        assertEquals(60L * 60 * 1000, delay)
    }

    @Test
    fun `delayUntilNext pomera na sutra ako je vreme vec proslo`() {
        val now = LocalDateTime.of(2026, 7, 26, 10, 0)
        val delay = ReminderScheduling.delayUntilNext("09:00", now)
        // 23 sata do istog vremena sutra.
        assertEquals(23L * 60 * 60 * 1000, delay)
    }

    @Test
    fun `delayUntilNext pomera na sutra kad je vreme identicno trenutnom`() {
        val now = LocalDateTime.of(2026, 7, 26, 9, 0)
        val delay = ReminderScheduling.delayUntilNext("09:00", now)
        assertEquals(24L * 60 * 60 * 1000, delay)
    }
}
