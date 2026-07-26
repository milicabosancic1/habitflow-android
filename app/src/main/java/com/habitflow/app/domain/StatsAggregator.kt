package com.habitflow.app.domain

import com.habitflow.app.data.local.HabitEntity
import com.habitflow.app.data.local.HabitEntryEntity
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

data class ChartPoint(val label: String, val successPct: Int)
data class CategoryStat(val category: String, val successPct: Int)

/** Čiste agregatne funkcije za Statistika ekran — sav račun u memoriji, bez SQL-a. */
object StatsAggregator {

    private val MONTH_LABELS = listOf(
        "Jan", "Feb", "Mar", "Apr", "Maj", "Jun", "Jul", "Avg", "Sep", "Okt", "Nov", "Dec"
    )

    fun completionPct(
        habits: List<HabitEntity>,
        entriesByHabit: Map<String, List<HabitEntryEntity>>,
        start: LocalDate,
        end: LocalDate
    ): Int {
        if (habits.isEmpty() || end.isBefore(start)) return 0
        val doneCount = habits.sumOf { habit ->
            entriesByHabit[habit.id].orEmpty().count { entry ->
                entry.status == EntryStatus.DONE &&
                    DateUtils.parse(entry.date).let { it >= start && it <= end }
            }
        }
        val days = ChronoUnit.DAYS.between(start, end) + 1
        val possible = habits.size * days
        if (possible <= 0) return 0
        return (doneCount.toFloat() / possible * 100).roundToInt()
    }

    fun overallSuccessPct(
        habits: List<HabitEntity>,
        entriesByHabit: Map<String, List<HabitEntryEntity>>,
        today: LocalDate
    ): Int = completionPct(habits, entriesByHabit, today.minusDays(29), today)

    fun trendDeltaPct(
        habits: List<HabitEntity>,
        entriesByHabit: Map<String, List<HabitEntryEntity>>,
        today: LocalDate
    ): Int {
        val thisWeek = completionPct(habits, entriesByHabit, today.minusDays(6), today)
        val lastWeek = completionPct(habits, entriesByHabit, today.minusDays(13), today.minusDays(7))
        return thisWeek - lastWeek
    }

    fun weeklyChart(
        habits: List<HabitEntity>,
        entriesByHabit: Map<String, List<HabitEntryEntity>>,
        today: LocalDate
    ): List<ChartPoint> = (7 downTo 0).map { i ->
        val end = today.minusDays(7L * i)
        val start = end.minusDays(6)
        val pct = completionPct(habits, entriesByHabit, start, end)
        ChartPoint(label = "${end.dayOfMonth}.${end.monthValue}", successPct = pct)
    }

    fun monthlyChart(
        habits: List<HabitEntity>,
        entriesByHabit: Map<String, List<HabitEntryEntity>>,
        today: LocalDate
    ): List<ChartPoint> = (5 downTo 0).map { m ->
        val month = YearMonth.from(today).minusMonths(m.toLong())
        val start = month.atDay(1)
        val monthEnd = month.atEndOfMonth()
        val end = if (monthEnd.isAfter(today)) today else monthEnd
        val pct = completionPct(habits, entriesByHabit, start, end)
        ChartPoint(label = MONTH_LABELS[month.monthValue - 1], successPct = pct)
    }

    fun categoryStats(
        habits: List<HabitEntity>,
        entriesByHabit: Map<String, List<HabitEntryEntity>>,
        today: LocalDate
    ): List<CategoryStat> {
        val start = today.minusDays(29)
        return habits.groupBy { it.category }.map { (category, habitsInCategory) ->
            CategoryStat(category, completionPct(habitsInCategory, entriesByHabit, start, today))
        }
    }
}
