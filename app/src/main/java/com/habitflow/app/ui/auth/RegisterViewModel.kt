package com.habitflow.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitflow.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val displayName: String = "",
    val identityStatement: String = "",
    val loading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) { _uiState.update { it.copy(email = value, error = null) } }
    fun onPasswordChange(value: String) { _uiState.update { it.copy(password = value, error = null) } }
    fun onDisplayNameChange(value: String) { _uiState.update { it.copy(displayName = value, error = null) } }
    fun onIdentityStatementChange(value: String) { _uiState.update { it.copy(identityStatement = value, error = null) } }

    fun register(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.email.isBlank() || state.password.isBlank() || state.displayName.isBlank()) return

        _uiState.update { it.copy(loading = true, error = null) }
        viewModelScope.launch {
            authRepository.register(
                email = state.email.trim(),
                password = state.password,
                displayName = state.displayName.trim(),
                identityStatement = state.identityStatement.trim()
            ).onSuccess {
                _uiState.update { it.copy(loading = false) }
                onSuccess()
            }.onFailure {
                _uiState.update { it.copy(loading = false, error = "Greška, pokušaj ponovo.") }
            }
        }
    }
}
