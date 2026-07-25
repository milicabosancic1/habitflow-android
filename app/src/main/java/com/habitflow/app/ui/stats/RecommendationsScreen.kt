package com.habitflow.app.ui.stats

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.habitflow.app.ui.common.EmptyState
import com.habitflow.app.ui.common.RecommendationCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendationsScreen(viewModel: RecommendationsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Preporuke") }) }
    ) { padding ->
        if (state.recommendations.isEmpty() && !state.loading) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                EmptyState(
                    title = "Još nema preporuka",
                    subtitle = "Kada budeš pratila navike nekoliko dana, ovde će se pojaviti " +
                        "pametni predlozi zasnovani na principima iz knjige Atomic Habits."
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.recommendations, key = { it.id }) { recommendation ->
                    RecommendationCard(
                        recommendation = recommendation,
                        onDismiss = { viewModel.dismiss(recommendation.id) }
                    )
                }
            }
        }
    }
}
