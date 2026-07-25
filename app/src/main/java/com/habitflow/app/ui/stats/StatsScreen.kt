package com.habitflow.app.ui.stats

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.habitflow.app.ui.common.EmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen() {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Statistika") }) }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Faza 6: grafikoni napretka, procenat uspešnosti po navici,
            // bedževi (achievements), nedeljni/mesečni pregled.
            EmptyState(
                title = "Uskoro",
                subtitle = "Ovde će biti grafikoni napretka, procenat uspešnosti i bedževi. " +
                        "Za sada statistiku svake navike možeš videti otvaranjem navike sa početnog ekrana."
            )
        }
    }
}
