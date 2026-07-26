package com.habitflow.app.ui.stats

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.habitflow.app.domain.AchievementType
import com.habitflow.app.ui.common.AchievementBadge
import com.habitflow.app.ui.common.CategoryStatChart
import com.habitflow.app.ui.common.EmptyState
import com.habitflow.app.ui.common.StatChart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(viewModel: StatsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Statistika") }) }
    ) { padding ->
        if (state.chartPoints.isEmpty() && !state.loading && state.overallSuccessPct == 0 && state.categoryStats.isEmpty()) {
            Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
                EmptyState(
                    title = "Uskoro",
                    subtitle = "Kada dodaš i pratiš navike nekoliko dana, ovde će se pojaviti grafikoni napretka i bedževi."
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize().padding(padding)
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Column {
                        Text("Ukupna uspešnost", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                "${state.overallSuccessPct}%",
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(trendText(state.trendDeltaPct), style = MaterialTheme.typography.bodyMedium, color = trendColor(state.trendDeltaPct))
                        }
                    }
                }

                item(span = { GridItemSpan(maxLineSpan) }) {
                    Column {
                        Text("Napredak", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        SingleChoiceSegmentedButtonRow {
                            SegmentedButton(
                                selected = state.chartPeriod == ChartPeriod.WEEKLY,
                                onClick = { viewModel.onPeriodChange(ChartPeriod.WEEKLY) },
                                shape = SegmentedButtonDefaults.itemShape(0, 2)
                            ) { Text("Nedeljno") }
                            SegmentedButton(
                                selected = state.chartPeriod == ChartPeriod.MONTHLY,
                                onClick = { viewModel.onPeriodChange(ChartPeriod.MONTHLY) },
                                shape = SegmentedButtonDefaults.itemShape(1, 2)
                            ) { Text("Mesečno") }
                        }
                        Spacer(Modifier.height(8.dp))
                        StatChart(points = state.chartPoints)
                    }
                }

                if (state.categoryStats.isNotEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Column {
                            Text("Uspešnost po kategorijama", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(8.dp))
                            CategoryStatChart(stats = state.categoryStats)
                        }
                    }
                }

                item(span = { GridItemSpan(maxLineSpan) }) {
                    Column {
                        Text("Bedževi", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Otključano ${state.unlockedTypes.size}/${AchievementType.entries.size}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                items(AchievementType.entries.sortedByDescending { it in state.unlockedTypes }) { type ->
                    AchievementBadge(
                        type = type,
                        unlocked = type in state.unlockedTypes,
                        unlockedAt = state.unlockedTypes[type]
                    )
                }
            }
        }
    }
}

private fun trendText(delta: Int): String = when {
    delta > 0 -> "+$delta% u odnosu na prošlu nedelju"
    delta < 0 -> "$delta% u odnosu na prošlu nedelju"
    else -> "Isto kao prošle nedelje"
}

@Composable
private fun trendColor(delta: Int) = when {
    delta > 0 -> MaterialTheme.colorScheme.primary
    delta < 0 -> MaterialTheme.colorScheme.error
    else -> MaterialTheme.colorScheme.onSurfaceVariant
}
