package com.habitflow.app.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitflow.app.data.local.RecommendationEntity
import com.habitflow.app.data.repository.RecommendationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RecommendationsUiState(
    val recommendations: List<RecommendationEntity> = emptyList(),
    val loading: Boolean = true
)

@HiltViewModel
class RecommendationsViewModel @Inject constructor(
    private val repository: RecommendationRepository
) : ViewModel() {

    val uiState: StateFlow<RecommendationsUiState> =
        repository.observeActive()
            .map { RecommendationsUiState(recommendations = it, loading = false) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = RecommendationsUiState()
            )

    fun dismiss(id: String) {
        viewModelScope.launch { repository.dismiss(id) }
    }
}
