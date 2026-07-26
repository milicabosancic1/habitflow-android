package com.habitflow.app.ui.home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.habitflow.app.data.local.HabitEntity
import com.habitflow.app.ui.common.DayProgressRing
import com.habitflow.app.ui.common.EmptyState
import com.habitflow.app.ui.common.RecommendationCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddHabit: () -> Unit,
    onOpenHabit: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddHabit) {
                Icon(Icons.Rounded.Add, contentDescription = "Dodaj naviku")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    "Danas",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
            }

            if (state.totalCount > 0) {
                item {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        DayProgressRing(
                            progress = state.progress,
                            completed = state.completedCount,
                            total = state.totalCount
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }

            val topRecommendation = state.topRecommendation
            if (topRecommendation != null) {
                item {
                    RecommendationCard(
                        recommendation = topRecommendation,
                        onDismiss = { viewModel.dismissRecommendation(topRecommendation.id) }
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }

            if (state.habits.isEmpty() && !state.loading) {
                item {
                    EmptyState(
                        title = "Nemaš još nijednu naviku",
                        subtitle = "Dodaj prvu naviku i počni da gradiš identitet koji želiš.",
                        actionLabel = "Dodaj naviku",
                        onAction = onAddHabit
                    )
                }
            } else {
                items(state.habits, key = { it.id }) { habit ->
                    HabitCard(
                        habit = habit,
                        done = state.doneHabitIds.contains(habit.id),
                        onToggle = { viewModel.toggleDone(habit.id) },
                        onClick = { onOpenHabit(habit.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun HabitCard(
    habit: HabitEntity,
    done: Boolean,
    onToggle: () -> Unit,
    onClick: () -> Unit
) {
    val cardScale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.graphicsLayer {
            scaleX = cardScale.value
            scaleY = cardScale.value
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    habit.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (done) {
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.85f)
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
                Spacer(Modifier.height(4.dp))
                AssistChip(
                    onClick = {},
                    label = { Text(habit.category) }
                )
            }
            HabitCheckToggle(
                done = done,
                onToggle = {
                    val justCompleted = !done
                    onToggle()
                    if (justCompleted) {
                        scope.launch {
                            // kartica blago pulsira pri završavanju navike
                            cardScale.animateTo(1.03f, tween(150))
                            cardScale.animateTo(1f, tween(150))
                        }
                    }
                }
            )
        }
    }
}

/**
 * Check dugme po design-system.md: krug 42dp, neoznačeno = prazan obris,
 * označeno = pun sage krug sa belom kvačicom i "pop" animacijom (1.0→1.15→1.05, ~0.35s).
 */
@Composable
private fun HabitCheckToggle(done: Boolean, onToggle: () -> Unit) {
    val scale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .size(42.dp)
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            }
            .clip(CircleShape)
            .then(
                if (done) {
                    Modifier.background(MaterialTheme.colorScheme.primary)
                } else {
                    Modifier.border(2.5.dp, MaterialTheme.colorScheme.primaryContainer, CircleShape)
                }
            )
            .clickable {
                val justCompleted = !done
                onToggle()
                if (justCompleted) {
                    scope.launch {
                        scale.animateTo(1.15f, tween(120))
                        scale.animateTo(1.05f, tween(120))
                        scale.animateTo(1f, tween(110))
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        if (done) {
            Icon(
                Icons.Rounded.Check,
                contentDescription = "Završeno",
                tint = Color.White
            )
        }
    }
}
