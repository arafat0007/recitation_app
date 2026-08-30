package com.example.recitation_app.feature_doa.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recitation_app.data.repository.DoaRepositoryImpl
import com.example.recitation_app.domain.model.PostSalahAdhkar
import com.example.recitation_app.domain.model.Salah
import com.example.recitation_app.domain.repository.DoaRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class PostSalahAdhkarState {
    object Selection : PostSalahAdhkarState()
    object Loading : PostSalahAdhkarState()
    data class Success(val items: List<PostSalahAdhkar>) : PostSalahAdhkarState()
    data class Error(val message: String) : PostSalahAdhkarState()
}

class DoaViewModel(
    private val repository: DoaRepository = DoaRepositoryImpl()
) : ViewModel() {

    private val _selectedSalah = MutableStateFlow(Salah.FAJR)
    val selectedSalah = _selectedSalah.asStateFlow()

    private val _postSalahAdhkarState = MutableStateFlow<PostSalahAdhkarState>(PostSalahAdhkarState.Selection)
    val postSalahAdhkarState = _postSalahAdhkarState.asStateFlow()

    // Session-based completion tracking (set of completed IDs)
    private val _completedAdhkars = MutableStateFlow<Set<String>>(emptySet())
    val completedAdhkars = _completedAdhkars.asStateFlow()

    fun showSelection() {
        _postSalahAdhkarState.value = PostSalahAdhkarState.Selection
        _completedAdhkars.value = emptySet() // Reset completion status when returning to menu
    }

    fun selectSalah(salah: Salah) {
        _selectedSalah.value = salah
        loadAdhkarForSalah(salah)
    }

    private fun loadAdhkarForSalah(salah: Salah) {
        viewModelScope.launch {
            _postSalahAdhkarState.value = PostSalahAdhkarState.Loading
            repository.getPostSalahAdhkar(salah)
                .catch { e -> _postSalahAdhkarState.value = PostSalahAdhkarState.Error(e.message ?: "Error") }
                .collect { items ->
                    _postSalahAdhkarState.value = PostSalahAdhkarState.Success(items)
                }
        }
    }

    fun getAdhkarById(id: String) = repository.getAdhkarById(id)

    fun markAsCompleted(id: String, salahKey: String) {
        _completedAdhkars.value += "${salahKey}_$id"
    }
}
