package com.habitflow.app.ui.stats

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.habitflow.app.ui.common.EmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendationsScreen() {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Preporuke") }) }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Faza 5: rule-based preporuke (najbolje vreme, olakšaj naviku,
            // upozorenje na prekid niza, predlog nove navike, nedeljni uvid).
            // Logika je opisana u docs/recommendations.md.
            EmptyState(
                title = "Još nema preporuka",
                subtitle = "Kada budeš pratila navike nekoliko dana, ovde će se pojaviti " +
                        "pametni predlozi zasnovani na principima iz knjige Atomic Habits."
            )
        }
    }
}
