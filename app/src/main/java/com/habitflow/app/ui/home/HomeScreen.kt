package com.habitflow.app.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.habitflow.app.data.local.HabitEntity
import com.habitflow.app.ui.common.DayProgressRing
import com.habitflow.app.ui.common.EmptyState

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
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(habit.name, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                AssistChip(
                    onClick = {},
                    label = { Text(habit.category) }
                )
            }
            FilledIconToggleButton(
                checked = done,
                onCheckedChange = { onToggle() }
            ) {
                Icon(
                    Icons.Rounded.Check,
                    contentDescription = if (done) "Završeno" else "Označi kao završeno"
                )
            }
        }
    }
}
