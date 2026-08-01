package com.habitflow.app.ui.home

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.habitflow.app.data.local.HabitEntity
import com.habitflow.app.data.local.HabitEntryEntity
import com.habitflow.app.domain.DateUtils
import com.habitflow.app.domain.EntryStatus
import com.habitflow.app.domain.GreetingHelper
import com.habitflow.app.domain.TrackingType
import com.habitflow.app.ui.common.DayProgressRing
import com.habitflow.app.ui.common.EmptyState
import com.habitflow.app.ui.common.RecommendationCard
import kotlinx.coroutines.launch
import java.time.LocalDate

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
                val greeting = GreetingHelper.greeting()
                val name = state.displayName
                Text(
                    if (name != null) "$greeting, $name! 🌱" else "$greeting! 🌱",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    state.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(16.dp))
                WeekStrip(selectedDate = state.selectedDate, onSelectDate = viewModel::selectDate)
                Spacer(Modifier.height(12.dp))
                Text(
                    if (state.isToday) {
                        "Navike za danas"
                    } else {
                        "Navike za ${DateUtils.displayLabel(DateUtils.parse(state.selectedDate))}"
                    },
                    style = MaterialTheme.typography.titleMedium,
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
            if (state.isToday && topRecommendation != null) {
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
                        entry = state.entriesById[habit.id],
                        onToggle = { viewModel.toggleDone(habit.id) },
                        onLogValue = { value -> viewModel.logValue(habit.id, value) },
                        onClick = { onOpenHabit(habit.id) }
                    )
                }
            }
        }
    }
}

/** Traka od 7 dana (nedelja koja sadrži [selectedDate]) sa strelicama za susedne nedelje. */
@Composable
private fun WeekStrip(selectedDate: String, onSelectDate: (String) -> Unit) {
    val selected = DateUtils.parse(selectedDate)
    val monday = DateUtils.mondayOf(selected)
    val today = LocalDate.now()

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { onSelectDate(DateUtils.format(selected.minusDays(7))) }) {
            Icon(Icons.Rounded.ChevronLeft, contentDescription = "Prethodna nedelja")
        }
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            for (i in 0 until 7) {
                val date = monday.plusDays(i.toLong())
                val isSelected = date == selected
                val isToday = date == today

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onSelectDate(DateUtils.format(date)) }
                        .then(
                            if (isSelected) {
                                Modifier.background(MaterialTheme.colorScheme.primary)
                            } else {
                                Modifier
                            }
                        )
                        .padding(vertical = 8.dp, horizontal = 6.dp)
                ) {
                    Text(
                        DateUtils.shortDayName(date),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                    Text(
                        "${date.dayOfMonth}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                    Box(Modifier.height(8.dp), contentAlignment = Alignment.Center) {
                        if (isToday && !isSelected) {
                            Box(
                                Modifier
                                    .size(4.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                        }
                    }
                }
            }
        }
        IconButton(onClick = { onSelectDate(DateUtils.format(selected.plusDays(7))) }) {
            Icon(Icons.Rounded.ChevronRight, contentDescription = "Sledeća nedelja")
        }
    }
}

@Composable
private fun HabitCard(
    habit: HabitEntity,
    entry: HabitEntryEntity?,
    onToggle: () -> Unit,
    onLogValue: (Int) -> Unit,
    onClick: () -> Unit
) {
    val done = entry?.status == EntryStatus.DONE
    val cardScale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()
    var wasDone by remember { mutableStateOf(done) }

    LaunchedEffect(done) {
        if (done && !wasDone) {
            // kartica blago pulsira pri završavanju navike
            cardScale.animateTo(1.03f, tween(150))
            cardScale.animateTo(1f, tween(150))
        }
        wasDone = done
    }

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
        Column {
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
                when (habit.trackingType) {
                    TrackingType.SIMPLE -> HabitCheckToggle(done = done, onToggle = onToggle)
                    TrackingType.QUANTITY -> HabitQuantityControl(
                        value = entry?.value ?: 0,
                        target = habit.targetCount,
                        unit = habit.unit,
                        onIncrement = { onLogValue((entry?.value ?: 0) + (habit.incrementAmount ?: 1)) }
                    )
                    TrackingType.NUMERIC -> HabitNumericControl(
                        value = entry?.value ?: 0,
                        target = habit.targetCount,
                        unit = habit.unit,
                        onSetValue = onLogValue
                    )
                }
            }
            if (habit.trackingType != TrackingType.SIMPLE) {
                HabitFillBar(value = entry?.value ?: 0, target = habit.targetCount)
            }
        }
    }
}

/** Traka napretka na dnu kartice — vizuelno "punjenje" prema dnevnom cilju (količina/broj). */
@Composable
private fun HabitFillBar(value: Int, target: Int) {
    val fraction = if (target <= 0) 0f else (value.toFloat() / target).coerceIn(0f, 1f)
    val animated by animateFloatAsState(targetValue = fraction, animationSpec = tween(400), label = "fill")
    val color = if (fraction >= 1f) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary

    LinearProgressIndicator(
        progress = { animated },
        color = color,
        trackColor = MaterialTheme.colorScheme.surfaceVariant,
        strokeCap = StrokeCap.Round,
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
    )
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

/** Krug koji na svaki tap dodaje fiksan iznos (npr. +250ml), sa prikazom "{value}/{target} {unit}". */
@Composable
private fun HabitQuantityControl(value: Int, target: Int, unit: String?, onIncrement: () -> Unit) {
    val done = value >= target
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .then(
                    if (done) {
                        Modifier.background(MaterialTheme.colorScheme.primary)
                    } else {
                        Modifier.border(2.5.dp, MaterialTheme.colorScheme.primaryContainer, CircleShape)
                    }
                )
                .clickable(onClick = onIncrement),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                if (done) Icons.Rounded.Check else Icons.Rounded.Add,
                contentDescription = if (done) "Završeno" else "Dodaj",
                tint = if (done) Color.White else MaterialTheme.colorScheme.primary
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(quantityLabel(value, target, unit), style = MaterialTheme.typography.labelSmall)
    }
}

/** Krug koji tapom otvara dijalog za ručni unos broja (npr. koraci), sa istim prikazom napretka. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HabitNumericControl(value: Int, target: Int, unit: String?, onSetValue: (Int) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    val done = value >= target

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .then(
                    if (done) {
                        Modifier.background(MaterialTheme.colorScheme.primary)
                    } else {
                        Modifier.border(2.5.dp, MaterialTheme.colorScheme.primaryContainer, CircleShape)
                    }
                )
                .clickable { showDialog = true },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                if (done) Icons.Rounded.Check else Icons.Rounded.Edit,
                contentDescription = if (done) "Završeno" else "Unesi broj",
                tint = if (done) Color.White else MaterialTheme.colorScheme.primary
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(quantityLabel(value, target, unit), style = MaterialTheme.typography.labelSmall)
    }

    if (showDialog) {
        var text by remember { mutableStateOf(if (value > 0) value.toString() else "") }
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Unesi vrednost") },
            text = {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it.filter { c -> c.isDigit() } },
                    label = { Text(unit ?: "Vrednost") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    text.toIntOrNull()?.let(onSetValue)
                    showDialog = false
                }) { Text("Sačuvaj") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Otkaži") }
            }
        )
    }
}

private fun quantityLabel(value: Int, target: Int, unit: String?): String =
    if (unit.isNullOrBlank()) "$value/$target" else "$value/$target $unit"
