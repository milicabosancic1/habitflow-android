@file:OptIn(ExperimentalCoroutinesApi::class)

package com.habitflow.app.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitflow.app.data.local.AchievementEntity
import com.habitflow.app.data.local.HabitEntity
import com.habitflow.app.data.local.HabitEntryEntity
import com.habitflow.app.data.repository.AchievementRepository
import com.habitflow.app.data.repository.HabitRepository
import com.habitflow.app.domain.AchievementType
import com.habitflow.app.domain.CategoryStat
import com.habitflow.app.domain.ChartPoint
import com.habitflow.app.domain.StatsAggregator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import javax.inject.Inject

enum class ChartPeriod { WEEKLY, MONTHLY }

data class StatsUiState(
    val loading: Boolean = true,
    val overallSuccessPct: Int = 0,
    val trendDeltaPct: Int = 0,
    val chartPeriod: ChartPeriod = ChartPeriod.WEEKLY,
    val chartPoints: List<ChartPoint> = emptyList(),
    val categoryStats: List<CategoryStat> = emptyList(),
    val unlockedTypes: Map<AchievementType, Long> = emptyMap()
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val habitRepository: HabitRepository,
    private val achievementRepository: AchievementRepository
) : ViewModel() {

    private val periodFlow = MutableStateFlow(ChartPeriod.WEEKLY)

    val uiState: StateFlow<StatsUiState> =
        combine(
            habitRepository.observeActiveHabits(),
            achievementRepository.observeAll(),
            periodFlow
        ) { habits, achievements, period -> Triple(habits, achievements, period) }
            .flatMapLatest { (habits, achievements, period) ->
                if (habits.isEmpty()) {
                    flowOf(buildUiState(emptyList(), emptyMap(), achievements, period))
                } else {
                    combine(habits.map { habitRepository.observeEntriesForHabit(it.id) }) { entriesArrays ->
                        val entriesByHabit = habits.mapIndexed { i, h -> h.id to entriesArrays[i] }.toMap()
                        buildUiState(habits, entriesByHabit, achievements, period)
                    }
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = StatsUiState()
            )

    fun onPeriodChange(period: ChartPeriod) {
        periodFlow.value = period
    }

    private fun buildUiState(
        habits: List<HabitEntity>,
        entriesByHabit: Map<String, List<HabitEntryEntity>>,
        achievements: List<AchievementEntity>,
        period: ChartPeriod
    ): StatsUiState {
        val today = LocalDate.now()
        val chartPoints = when (period) {
            ChartPeriod.WEEKLY -> StatsAggregator.weeklyChart(habits, entriesByHabit, today)
            ChartPeriod.MONTHLY -> StatsAggregator.monthlyChart(habits, entriesByHabit, today)
        }
        return StatsUiState(
            loading = false,
            overallSuccessPct = StatsAggregator.overallSuccessPct(habits, entriesByHabit, today),
            trendDeltaPct = StatsAggregator.trendDeltaPct(habits, entriesByHabit, today),
            chartPeriod = period,
            chartPoints = chartPoints,
            categoryStats = StatsAggregator.categoryStats(habits, entriesByHabit, today),
            unlockedTypes = achievements.associate { it.type to it.unlockedAt }
        )
    }
}
