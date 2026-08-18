package com.habitflow.app.domain

import com.habitflow.app.data.local.HabitEntity
import com.habitflow.app.data.local.HabitEntryEntity
import java.time.LocalDate
import java.time.YearMonth

/** Čista agregatna logika za Kalendar ekran — procenat završenosti po danu u mesecu. */
object MonthlyCalendar {

    /**
     * Za svaki dan u [month], procenat navika (od prosleđenih [habits]) koje su tog dana DONE.
     * Kad se prosledi sve aktivne navike → ukupan procenat; kad se prosledi samo jedna → filtrirano.
     */
    fun completionByDay(
        habits: List<HabitEntity>,
        entriesByHabit: Map<String, List<HabitEntryEntity>>,
        month: YearMonth
    ): Map<LocalDate, Float> {
        val doneDatesByHabit = habits.associate { h ->
            h.id to entriesByHabit[h.id].orEmpty()
                .filter { it.status == EntryStatus.DONE }
                .map { it.date }
                .toSet()
        }
        val result = mutableMapOf<LocalDate, Float>()
        var date = month.atDay(1)
        val end = month.atEndOfMonth()
        while (!date.isAfter(end)) {
            val dateStr = DateUtils.format(date)
            val scheduledHabits = habits.filter { HabitScheduling.isScheduledOn(it, date) }
            val doneCount = scheduledHabits.count { dateStr in doneDatesByHabit[it.id].orEmpty() }
            result[date] = if (scheduledHabits.isEmpty()) 0f else doneCount.toFloat() / scheduledHabits.size
            date = date.plusDays(1)
        }
        return result
    }
}
