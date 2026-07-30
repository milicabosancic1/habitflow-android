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
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val loading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, error = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, error = null) }
    }

    fun login(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.email.isBlank() || state.password.isBlank()) return

        _uiState.update { it.copy(loading = true, error = null) }
        viewModelScope.launch {
            authRepository.login(state.email.trim(), state.password)
                .onSuccess {
                    _uiState.update { it.copy(loading = false) }
                    onSuccess()
                }
                .onFailure { e ->
                    val message = when {
                        e is HttpException && e.code() == 401 -> "Pogrešni kredencijali."
                        e is IOException -> "Greška, pokušaj ponovo."
                        else -> "Greška, pokušaj ponovo."
                    }
                    _uiState.update { it.copy(loading = false, error = message) }
                }
        }
    }

    fun loginWithGoogle(idToken: String, email: String, displayName: String, onSuccess: () -> Unit) {
        _uiState.update { it.copy(loading = true, error = null) }
        viewModelScope.launch {
            authRepository.loginWithGoogle(idToken, email, displayName)
                .onSuccess {
                    _uiState.update { it.copy(loading = false) }
                    onSuccess()
                }
                .onFailure {
                    _uiState.update { it.copy(loading = false, error = "Greška, pokušaj ponovo.") }
                }
        }
    }

    fun onGoogleSignInFailed() {
        _uiState.update { it.copy(loading = false, error = "Google prijava nije uspela.") }
    }
}
