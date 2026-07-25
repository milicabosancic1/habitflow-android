package com.habitflow.app.ui.habit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.habitflow.app.domain.FrequencyType
import com.habitflow.app.domain.HabitType

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

            OutlinedTextField(
                value = targetText, onValueChange = { targetText = it.filter { c -> c.isDigit() } },
                label = { Text("Ciljna vrednost") },
                modifier = Modifier.fillMaxWidth()
            )
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
                        targetCount = targetText.toIntOrNull() ?: 1,
                        reminderTime = reminder,
                        cueText = cue,
                        onSaved = onDone
                    )
                },
                enabled = name.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) { Text("Sačuvaj naviku") }
        }
    }
}
