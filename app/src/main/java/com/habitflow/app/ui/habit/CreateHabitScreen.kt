package com.habitflow.app.ui.habit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.habitflow.app.domain.FrequencyType
import com.habitflow.app.domain.HABIT_COLORS
import com.habitflow.app.domain.HabitType
import com.habitflow.app.domain.SUGGESTED_HABITS
import com.habitflow.app.domain.TrackingType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateHabitScreen(
    onDone: () -> Unit,
    viewModel: CreateHabitViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(HabitType.BUILD) }
    var frequency by remember { mutableStateOf(FrequencyType.DAILY) }
    var targetText by remember { mutableStateOf("1") }
    var reminder by remember { mutableStateOf("") }
    var cue by remember { mutableStateOf("") }
    var trackingType by remember { mutableStateOf(TrackingType.SIMPLE) }
    var unit by remember { mutableStateOf("") }
    var incrementText by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(HABIT_COLORS[0]) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nova navika") },
                navigationIcon = {
                    IconButton(onClick = onDone) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Nazad")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column {
                Text("Predlozi", style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(SUGGESTED_HABITS) { suggestion ->
                        AssistChip(
                            onClick = {
                                name = suggestion.name
                                category = suggestion.category
                                trackingType = suggestion.trackingType
                                unit = suggestion.unit.orEmpty()
                                incrementText = suggestion.incrementAmount?.toString().orEmpty()
                                targetText = suggestion.targetCount.toString()
                                selectedColor = suggestion.color
                            },
                            label = { Text(suggestion.name) }
                        )
                    }
                }
            }

            Column {
                Text("Boja", style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    HABIT_COLORS.forEach { hex ->
                        val color = remember(hex) { Color(android.graphics.Color.parseColor(hex)) }
                        val isSelected = hex == selectedColor
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(color)
                                .then(
                                    if (isSelected) {
                                        Modifier.border(2.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                                    } else {
                                        Modifier
                                    }
                                )
                                .clickable { selectedColor = hex },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(Icons.Rounded.Check, contentDescription = "Izabrano", tint = Color.White)
                            }
                        }
                    }
                }
            }

            OutlinedTextField(
                value = name, onValueChange = { name = it },
                label = { Text("Naziv navike") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = category, onValueChange = { category = it },
                label = { Text("Kategorija (npr. Zdravlje, Učenje)") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Tip", style = MaterialTheme.typography.labelLarge)
            SingleChoiceSegmentedButtonRow {
                SegmentedButton(
                    selected = type == HabitType.BUILD,
                    onClick = { type = HabitType.BUILD },
                    shape = SegmentedButtonDefaults.itemShape(0, 2)
                ) { Text("Izgradi") }
                SegmentedButton(
                    selected = type == HabitType.QUIT,
                    onClick = { type = HabitType.QUIT },
                    shape = SegmentedButtonDefaults.itemShape(1, 2)
                ) { Text("Ostavi") }
            }

            Text("Učestalost", style = MaterialTheme.typography.labelLarge)
            SingleChoiceSegmentedButtonRow {
                FrequencyType.entries.forEachIndexed { i, f ->
                    SegmentedButton(
                        selected = frequency == f,
                        onClick = { frequency = f },
                        shape = SegmentedButtonDefaults.itemShape(i, FrequencyType.entries.size)
                    ) {
                        Text(
                            when (f) {
                                FrequencyType.DAILY -> "Dnevno"
                                FrequencyType.SPECIFIC_DAYS -> "Dani"
                                FrequencyType.TIMES_PER_WEEK -> "N/ned."
                            }
                        )
                    }
                }
            }

            Text("Način praćenja", style = MaterialTheme.typography.labelLarge)
            SingleChoiceSegmentedButtonRow {
                SegmentedButton(
                    selected = trackingType == TrackingType.SIMPLE,
                    onClick = { trackingType = TrackingType.SIMPLE },
                    shape = SegmentedButtonDefaults.itemShape(0, 3)
                ) { Text("Da/Ne") }
                SegmentedButton(
                    selected = trackingType == TrackingType.QUANTITY,
                    onClick = { trackingType = TrackingType.QUANTITY },
                    shape = SegmentedButtonDefaults.itemShape(1, 3)
                ) { Text("Količina") }
                SegmentedButton(
                    selected = trackingType == TrackingType.NUMERIC,
                    onClick = { trackingType = TrackingType.NUMERIC },
                    shape = SegmentedButtonDefaults.itemShape(2, 3)
                ) { Text("Broj") }
            }

            if (trackingType != TrackingType.SIMPLE) {
                OutlinedTextField(
                    value = unit, onValueChange = { unit = it },
                    label = { Text("Jedinica (npr. ml, koraka)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = targetText, onValueChange = { targetText = it.filter { c -> c.isDigit() } },
                    label = { Text("Dnevni cilj") },
                    modifier = Modifier.fillMaxWidth()
                )
                if (trackingType == TrackingType.QUANTITY) {
                    OutlinedTextField(
                        value = incrementText, onValueChange = { incrementText = it.filter { c -> c.isDigit() } },
                        label = { Text("Iznos po kliku") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            OutlinedTextField(
                value = reminder, onValueChange = { reminder = it },
                label = { Text("Podsetnik (HH:mm, opciono)") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = cue, onValueChange = { cue = it },
                label = { Text("Nakon čega? (habit stacking, opciono)") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    viewModel.save(
                        name = name,
                        category = category,
                        type = type,
                        frequencyType = frequency,
                        targetCount = if (trackingType == TrackingType.SIMPLE) 1 else (targetText.toIntOrNull() ?: 1),
                        reminderTime = reminder,
                        cueText = cue,
                        trackingType = trackingType,
                        unit = if (trackingType == TrackingType.SIMPLE) null else unit,
                        incrementAmount = if (trackingType == TrackingType.QUANTITY) incrementText.toIntOrNull() else null,
                        color = selectedColor,
                        onSaved = onDone
                    )
                },
                enabled = name.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) { Text("Sačuvaj naviku") }
        }
    }
}
