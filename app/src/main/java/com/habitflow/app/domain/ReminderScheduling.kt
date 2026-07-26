package com.habitflow.app.domain

import java.time.Duration
import java.time.LocalDateTime

object ReminderScheduling {

    /** Kašnjenje u ms do sledećeg pojavljivanja "HH:mm" (danas ako nije prošlo, inače sutra). */
    fun delayUntilNext(reminderTime: String, now: LocalDateTime = LocalDateTime.now()): Long {
        val (hour, minute) = reminderTime.split(":").map { it.toInt() }
        var target = now.toLocalDate().atTime(hour, minute)
        if (!target.isAfter(now)) target = target.plusDays(1)
        return Duration.between(now, target).toMillis()
    }
}
