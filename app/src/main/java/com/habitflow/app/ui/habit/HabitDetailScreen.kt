package com.habitflow.app.ui.habit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Archive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.habitflow.app.domain.DateUtils
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitDetailScreen(
    habitId: String,
    onBack: () -> Unit,
    viewModel: HabitDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.habit?.name ?: "Navika") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Nazad")
                    }
                },
                actions = {
                    if (state.habit != null) {
                        IconButton(onClick = { viewModel.archive(onBack) }) {
                            Icon(Icons.Rounded.Archive, contentDescription = "Arhiviraj")
                        }
                    }
                }
            )
        }
    ) { padding ->
        val habit = state.habit
        if (habit == null) {
            Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                if (state.loading) CircularProgressIndicator()
                else Text("Navika nije pronađena.")
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Kategorija + tip
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(onClick = {}, label = { Text(habit.category) })
                AssistChip(onClick = {}, label = {
                    Text(if (habit.type.name == "BUILD") "Izgradnja" else "Ostavljanje")
                })
            }

            // Statistika: tri kartice
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("Trenutni niz", "${state.currentStreak}", "dana", Modifier.weight(1f))
                StatCard("Najduži niz", "${state.longestStreak}", "dana", Modifier.weight(1f))
                StatCard("Ukupno", "${state.totalDone}", "puta", Modifier.weight(1f))
            }

            // Heatmap poslednjih 12 nedelja
            Text("Poslednjih 12 nedelja", style = MaterialTheme.typography.titleMedium)
            Heatmap(doneDates = state.doneDates)

            if (habit.cueText != null) {
                Card(shape = RoundedCornerShape(16.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Okidač (cue)", style = MaterialTheme.typography.labelMedium)
                        Spacer(Modifier.height(4.dp))
                        Text(habit.cueText, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, unit: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(16.dp)) {
        Column(
            Modifier.padding(vertical = 16.dp, horizontal = 8.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(unit, style = MaterialTheme.typography.labelSmall)
            Spacer(Modifier.height(4.dp))
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Jednostavan heatmap: 12 kolona (nedelje) x 7 redova (dani).
 * Popunjeno polje = navika urađena tog dana.
 */
@Composable
private fun Heatmap(doneDates: Set<String>) {
    val weeks = 12
    val today = LocalDate.now()
    // Poravnaj na ponedeljak tekuće nedelje kao poslednju kolonu
    val startMonday = today.minusWeeks((weeks - 1).toLong())
        .with(java.time.DayOfWeek.MONDAY)

    val filledColor = MaterialTheme.colorScheme.primary
    val emptyColor = MaterialTheme.colorScheme.surfaceVariant

    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        for (w in 0 until weeks) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                for (d in 0 until 7) {
                    val date = startMonday.plusWeeks(w.toLong()).plusDays(d.toLong())
                    val isFuture = date.isAfter(today)
                    val done = doneDates.contains(DateUtils.format(date))
                    Box(
                        Modifier
                            .size(14.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                when {
                                    isFuture -> Color.Transparent
                                    done -> filledColor
                                    else -> emptyColor
                                }
                            )
                    )
                }
            }
        }
    }
}
