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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.habitflow.app.data.local.HabitEntity
import com.habitflow.app.domain.FrequencyType
import com.habitflow.app.domain.HABIT_COLORS
import com.habitflow.app.domain.HabitScheduling
import com.habitflow.app.domain.HabitType
import com.habitflow.app.domain.SUGGESTED_HABITS
import com.habitflow.app.domain.TrackingType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateHabitScreen(
    onDone: () -> Unit,
    viewModel: CreateHabitViewModel = hiltViewModel()
) {
    val existingHabit by viewModel.existingHabit.collectAsStateWithLifecycle()
    val stackableHabits by viewModel.stackableHabits.collectAsStateWithLifecycle()

    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(HabitType.BUILD) }
    var frequency by remember { mutableStateOf(FrequencyType.DAILY) }
    var selectedDays by remember { mutableStateOf(setOf(1)) }
    var weeklyTargetText by remember { mutableStateOf("3") }
    var targetText by remember { mutableStateOf("1") }
    var reminder by remember { mutableStateOf<String?>(null) }
    var cue by remember { mutableStateOf("") }
    var stackedAfterHabitId by remember { mutableStateOf<String?>(null) }
    var trackingType by remember { mutableStateOf(TrackingType.SIMPLE) }
    var unit by remember { mutableStateOf("") }
    var incrementText by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(HABIT_COLORS[0]) }

    // Popuni formu jednom kad se postojeća navika učita (mod izmene).
    LaunchedEffect(existingHabit?.id) {
        val habit = existingHabit ?: return@LaunchedEffect
        name = habit.name
        category = habit.category
        type = habit.type
        frequency = habit.frequencyType
        selectedDays = HabitScheduling.parseDays(habit.daysOfWeek).ifEmpty { setOf(1) }
        weeklyTargetText = (habit.weeklyTarget ?: 3).toString()
        targetText = habit.targetCount.toString()
        reminder = habit.reminderTime
        cue = habit.cueText.orEmpty()
        stackedAfterHabitId = habit.stackedAfterHabitId
        trackingType = habit.trackingType
        unit = habit.unit.orEmpty()
        incrementText = habit.incrementAmount?.toString().orEmpty()
        selectedColor = habit.color ?: HABIT_COLORS[0]
    }

    val isEditing = viewModel.isEditing
    // U modu izmene čekamo da se navika stvarno učita pre nego što dozvolimo čuvanje,
    // jer save() radi update nad `existingHabit.value`.
    val readyToSave = !isEditing || existingHabit != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Izmeni naviku" else "Nova navika") },
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
            if (!isEditing) {
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

            if (frequency == FrequencyType.SPECIFIC_DAYS) {
                DayOfWeekPicker(selectedDays = selectedDays, onToggle = { day ->
                    selectedDays = if (day in selectedDays) selectedDays - day else selectedDays + day
                })
            }
            if (frequency == FrequencyType.TIMES_PER_WEEK) {
                OutlinedTextField(
                    value = weeklyTargetText,
                    onValueChange = { weeklyTargetText = it.filter { c -> c.isDigit() } },
                    label = { Text("Koliko puta nedeljno") },
                    modifier = Modifier.fillMaxWidth()
                )
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

            ReminderPickerField(reminderTime = reminder, onTimeChange = { reminder = it })

            StackingPicker(
                habits = stackableHabits,
                selectedId = stackedAfterHabitId,
                onSelect = { stackedAfterHabitId = it }
            )
            OutlinedTextField(
                value = cue, onValueChange = { cue = it },
                label = { Text("Okidač (cue), opciono — npr. \"posle jutarnje kafe\"") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    viewModel.save(
                        name = name,
                        category = category,
                        type = type,
                        frequencyType = frequency,
                        daysOfWeek = if (frequency == FrequencyType.SPECIFIC_DAYS) {
                            HabitScheduling.formatDays(selectedDays)
                        } else null,
                        targetCount = if (trackingType == TrackingType.SIMPLE) 1 else (targetText.toIntOrNull() ?: 1),
                        reminderTime = reminder,
                        cueText = cue,
                        stackedAfterHabitId = stackedAfterHabitId,
                        trackingType = trackingType,
                        unit = if (trackingType == TrackingType.SIMPLE) null else unit,
                        incrementAmount = if (trackingType == TrackingType.QUANTITY) incrementText.toIntOrNull() else null,
                        color = selectedColor,
                        weeklyTarget = if (frequency == FrequencyType.TIMES_PER_WEEK) {
                            (weeklyTargetText.toIntOrNull() ?: 1).coerceIn(1, 7)
                        } else null,
                        onSaved = onDone
                    )
                },
                enabled = name.isNotBlank() &&
                    readyToSave &&
                    (frequency != FrequencyType.SPECIFIC_DAYS || selectedDays.isNotEmpty()),
                modifier = Modifier.fillMaxWidth()
            ) { Text(if (isEditing) "Sačuvaj izmene" else "Sačuvaj naviku") }
        }
    }
}

private val DAY_LETTERS = listOf("Pon", "Uto", "Sre", "Čet", "Pet", "Sub", "Ned")

@Composable
private fun DayOfWeekPicker(selectedDays: Set<Int>, onToggle: (Int) -> Unit) {
    Column {
        Text("Izaberi dane", style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            DAY_LETTERS.forEachIndexed { index, label ->
                val isoDay = index + 1
                FilterChip(
                    selected = isoDay in selectedDays,
                    onClick = { onToggle(isoDay) },
                    label = { Text(label) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReminderPickerField(reminderTime: String?, onTimeChange: (String?) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }

    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedButton(onClick = { showDialog = true }, modifier = Modifier.weight(1f)) {
                Text(reminderTime?.let { "Podsetnik u $it" } ?: "Podesi podsetnik (opciono)")
            }
            if (reminderTime != null) {
                TextButton(onClick = { onTimeChange(null) }) { Text("Ukloni") }
            }
        }
    }

    if (showDialog) {
        val initial = reminderTime?.split(":")?.mapNotNull { it.toIntOrNull() }
        val initialHour = initial?.getOrNull(0) ?: 9
        val initialMinute = initial?.getOrNull(1) ?: 0
        val state = rememberTimePickerState(initialHour = initialHour, initialMinute = initialMinute, is24Hour = true)
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    val hh = state.hour.toString().padStart(2, '0')
                    val mm = state.minute.toString().padStart(2, '0')
                    onTimeChange("$hh:$mm")
                    showDialog = false
                }) { Text("Potvrdi") }
            },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Otkaži") } },
            text = { TimePicker(state = state) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StackingPicker(
    habits: List<HabitEntity>,
    selectedId: String?,
    onSelect: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedName = habits.find { it.id == selectedId }?.name ?: "Bez veze"

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selectedName,
            onValueChange = {},
            readOnly = true,
            label = { Text("Habit stacking — nakon navike") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(text = { Text("Bez veze") }, onClick = { onSelect(null); expanded = false })
            habits.forEach { h ->
                DropdownMenuItem(text = { Text(h.name) }, onClick = { onSelect(h.id); expanded = false })
            }
        }
    }
}
