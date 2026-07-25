package com.habitflow.app.recommendation.rules

import com.habitflow.app.domain.EntryStatus
import com.habitflow.app.domain.RecommendationType
import com.habitflow.app.recommendation.RecommendationCandidate
import com.habitflow.app.recommendation.RecommendationContext
import com.habitflow.app.recommendation.RecommendationRule
import java.time.Instant
import java.time.ZoneId

/** 1. zakon (očiglednost): predlog najboljeg vremena za naviku. */
class OptimalTimeRule : RecommendationRule {
    override val type = RecommendationType.OPTIMAL_TIME

    override fun evaluate(context: RecommendationContext): List<RecommendationCandidate> {
        val candidates = mutableListOf<RecommendationCandidate>()

        for (habit in context.habits) {
            val doneEntries = context.entriesByHabit[habit.id]
                .orEmpty()
                .filter { it.status == EntryStatus.DONE }
            if (doneEntries.size < MIN_SAMPLES) continue

            val counts = doneEntries
                .groupingBy { bucketFor(it.updatedAt) }
                .eachCount()
            val dominant = counts.maxByOrNull { it.value } ?: continue
            val ratio = dominant.value.toFloat() / doneEntries.size
            if (ratio <= 0.6f) continue

            candidates += RecommendationCandidate(
                type = type,
                habitId = habit.id,
                message = "Najčešće završavaš '${habit.name}' ${dominant.key.label} — " +
                    "probaj da postaviš podsetnik za to vreme."
            )
        }

        return candidates
    }

    private fun bucketFor(updatedAt: Long): TimeBucket {
        val hour = Instant.ofEpochMilli(updatedAt).atZone(ZoneId.systemDefault()).hour
        return when (hour) {
            in 5..11 -> TimeBucket.JUTRO
            in 12..17 -> TimeBucket.POPODNE
            else -> TimeBucket.VECE
        }
    }

    private enum class TimeBucket(val label: String) {
        JUTRO("ujutru"), POPODNE("popodne"), VECE("uveče")
    }

    companion object {
        private const val MIN_SAMPLES = 5
    }
}
