package com.example.recitation_app.feature_auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recitation_app.data.repository.AuthRepositoryImpl
import com.example.recitation_app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    object ResetEmailSent : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(
    private val repository: AuthRepository = AuthRepositoryImpl()
) : ViewModel() {

    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state = _state.asStateFlow()

    val isLoggedIn = repository.isLoggedIn

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            repository.login(email, password)
                .onSuccess { _state.value = AuthState.Success }
                .onFailure { _state.value = AuthState.Error(it.message ?: "Login failed") }
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            repository.register(name, email, password)
                .onSuccess { _state.value = AuthState.Success }
                .onFailure { _state.value = AuthState.Error(it.message ?: "Registration failed") }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _state.value = AuthState.Idle
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            repository.resetPassword(email)
                .onSuccess { _state.value = AuthState.ResetEmailSent }
                .onFailure { _state.value = AuthState.Error(it.message ?: "Reset link failed") }
        }
    }
    
    fun resetState() {
        _state.value = AuthState.Idle
    }
}
