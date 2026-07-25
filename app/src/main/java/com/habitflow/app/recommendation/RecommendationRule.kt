package com.habitflow.app.recommendation

import com.habitflow.app.data.local.HabitEntity
import com.habitflow.app.data.local.HabitEntryEntity
import com.habitflow.app.domain.RecommendationType
import java.time.LocalDate

/**
 * Zajednički interfejs za sva pravila preporuka. Svako pravilo je posebna
 * klasa/strategija — vidi docs/recommendations.md (tabela pravilo → okidač → princip knjige).
 */
interface RecommendationRule {
    val type: RecommendationType
    fun evaluate(context: RecommendationContext): List<RecommendationCandidate>
}

data class RecommendationContext(
    val userId: String,
    val today: LocalDate,
    val habits: List<HabitEntity>,
    val entriesByHabit: Map<String, List<HabitEntryEntity>>
)

data class RecommendationCandidate(
    val type: RecommendationType,
    val habitId: String? = null,
    val category: String? = null,
    val message: String
)
