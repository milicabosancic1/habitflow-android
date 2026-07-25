package com.habitflow.app.recommendation

import com.habitflow.app.data.local.RecommendationEntity
import com.habitflow.app.domain.RecommendationType

/** Jedan izvor istine za prioritet preporuka — koristi ga engine (trim) i Home ekran (izbor kartice). */
object RecommendationPriority {

    fun rank(type: RecommendationType): Int = when (type) {
        RecommendationType.STREAK_WARNING -> 0
        RecommendationType.MAKE_EASIER -> 1
        else -> 2
    }

    fun sorted(list: List<RecommendationEntity>): List<RecommendationEntity> =
        list.sortedWith(compareBy({ rank(it.type) }, { -it.createdAt }))
}
