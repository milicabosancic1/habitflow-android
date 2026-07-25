package com.habitflow.app.recommendation.rules

import com.habitflow.app.domain.DateUtils
import com.habitflow.app.domain.EntryStatus
import com.habitflow.app.domain.RecommendationType
import com.habitflow.app.recommendation.RecommendationCandidate
import com.habitflow.app.recommendation.RecommendationContext
import com.habitflow.app.recommendation.RecommendationRule
import java.time.DayOfWeek
import java.time.LocalDate
import kotlin.math.roundToInt

/** 4. zakon (zadovoljstvo): nedeljni uvid u napredak, nedeljom. */
class WeeklyInsightRule : RecommendationRule {
    override val type = RecommendationType.WEEKLY_INSIGHT

    override fun evaluate(context: RecommendationContext): List<RecommendationCandidate> {
        if (context.today.dayOfWeek != DayOfWeek.SUNDAY || context.habits.isEmpty()) return emptyList()

        val thisWeekPct = completionPct(context, context.today.minusDays(6), context.today)
        val lastWeekPct = completionPct(context, context.today.minusDays(13), context.today.minusDays(7))
        val diff = thisWeekPct - lastWeekPct

        val message = when {
            diff > 0 -> "Ove nedelje si završila $thisWeekPct% navika — $diff% bolje nego prošle. Nastavi tako!"
            diff < 0 -> "Ove nedelje si završila $thisWeekPct% navika — ${-diff}% manje nego prošle. Sledeća nedelja je nova prilika!"
            else -> "Ove nedelje si završila $thisWeekPct% navika — isto kao prošle nedelje."
        }

        return listOf(RecommendationCandidate(type = type, message = message))
    }

    private fun completionPct(context: RecommendationContext, start: LocalDate, end: LocalDate): Int {
        val doneCount = context.habits.sumOf { habit ->
            context.entriesByHabit[habit.id].orEmpty().count { entry ->
                entry.status == EntryStatus.DONE &&
                    DateUtils.parse(entry.date).let { it >= start && it <= end }
            }
        }
        val possible = context.habits.size * 7
        if (possible == 0) return 0
        return (doneCount.toFloat() / possible * 100).roundToInt()
    }
}
