package com.habitflow.app.domain

import com.habitflow.app.data.local.HabitEntity
import java.time.LocalDate

/**
 * Čita/piše `HabitEntity.daysOfWeek` (CSV ISO brojeva dana, 1=ponedeljak..7=nedelja)
 * i odlučuje da li je navika zakazana za dati datum.
 */
object HabitScheduling {

    fun parseDays(raw: String?): Set<Int> =
        raw.orEmpty().split(",").mapNotNull { it.trim().toIntOrNull() }.toSet()

    fun formatDays(days: Set<Int>): String = days.sorted().joinToString(",")

    /**
     * DAILY i TIMES_PER_WEEK nemaju fiksne dane (korisnik sam bira kad će ih uraditi),
     * pa se prikazuju svaki dan. SPECIFIC_DAYS se prikazuje samo na izabrane dane.
     */
    fun isScheduledOn(frequencyType: FrequencyType, daysOfWeek: String?, date: LocalDate): Boolean =
        when (frequencyType) {
            FrequencyType.DAILY, FrequencyType.TIMES_PER_WEEK -> true
            FrequencyType.SPECIFIC_DAYS -> parseDays(daysOfWeek).contains(date.dayOfWeek.value)
        }

    fun isScheduledOn(habit: HabitEntity, date: LocalDate): Boolean =
        isScheduledOn(habit.frequencyType, habit.daysOfWeek, date)
}
