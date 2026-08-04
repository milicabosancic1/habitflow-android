package com.habitflow.app.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.habitflow.app.domain.DateUtils
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(viewModel: CalendarViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Kalendar") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = state.selectedHabitId == null,
                        onClick = { viewModel.selectHabit(null) },
                        label = { Text("Sve") }
                    )
                }
                items(state.habits, key = { it.id }) { habit ->
                    FilterChip(
                        selected = state.selectedHabitId == habit.id,
                        onClick = { viewModel.selectHabit(habit.id) },
                        label = { Text(habit.name) }
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = viewModel::previousMonth) {
                    Icon(Icons.Rounded.ChevronLeft, contentDescription = "Prethodni mesec")
                }
                Text(
                    DateUtils.monthYearLabel(state.month),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = viewModel::nextMonth) {
                    Icon(Icons.Rounded.ChevronRight, contentDescription = "Sledeći mesec")
                }
            }

            MonthGrid(month = state.month, completionByDay = state.completionByDay)
        }
    }
}

@Composable
private fun MonthGrid(month: YearMonth, completionByDay: Map<LocalDate, Float>) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            for (i in 0 until 7) {
                val date = month.atDay(1).with(DayOfWeek.of(i + 1))
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(
                        DateUtils.shortDayName(date),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        val firstDay = month.atDay(1)
        val leadingBlanks = firstDay.dayOfWeek.value - 1
        val totalDays = month.lengthOfMonth()
        val cells: List<LocalDate?> = List(leadingBlanks) { null } + (1..totalDays).map { month.atDay(it) }
        val today = LocalDate.now()

        cells.chunked(7).forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                for (i in 0 until 7) {
                    val date = week.getOrNull(i)
                    Box(Modifier.weight(1f).aspectRatio(1f).padding(2.dp), contentAlignment = Alignment.Center) {
                        if (date != null) {
                            DayCell(date = date, fraction = completionByDay[date] ?: 0f, isToday = date == today)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(date: LocalDate, fraction: Float, isToday: Boolean) {
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val fillColor = MaterialTheme.colorScheme.primary
    val backgroundColor = lerp(trackColor, fillColor, fraction)
    val isFuture = date.isAfter(LocalDate.now())

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(CircleShape)
            .background(backgroundColor)
            .then(
                if (isToday) Modifier.border(2.dp, MaterialTheme.colorScheme.onSurface, CircleShape) else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "${date.dayOfMonth}",
            style = MaterialTheme.typography.bodyMedium,
            color = if (isFuture) {
                MaterialTheme.colorScheme.onSurfaceVariant
            } else if (fraction > 0.5f) {
                Color.White
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
    }
}
