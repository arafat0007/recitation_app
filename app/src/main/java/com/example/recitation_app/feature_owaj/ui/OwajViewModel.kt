package com.example.recitation_app.feature_owaj.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recitation_app.data.repository.OwajRepositoryImpl
import com.example.recitation_app.domain.model.Owaj
import com.example.recitation_app.domain.repository.OwajRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class OwajListState {
    object Loading : OwajListState()
    data class Success(val owajs: List<Owaj>) : OwajListState()
    data class Error(val message: String) : OwajListState()
    object Empty : OwajListState()
}

sealed class OwajDetailState {
    object Loading : OwajDetailState()
    data class Success(val owaj: Owaj) : OwajDetailState()
    data class Error(val message: String) : OwajDetailState()
}

class OwajViewModel(
    private val repository: OwajRepository = OwajRepositoryImpl() // Using Real Repository
) : ViewModel() {

    private val _listState = MutableStateFlow<OwajListState>(OwajListState.Loading)
    val listState: StateFlow<OwajListState> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow<OwajDetailState>(OwajDetailState.Loading)
    val detailState: StateFlow<OwajDetailState> = _detailState.asStateFlow()

    init {
        loadOwajs()
    }

    fun loadOwajs() {
        Log.d("FLOW", "ViewModel: loadOwajs() called")
        viewModelScope.launch {
            repository.getOwajs()
                .onStart { 
                    Log.d("FLOW", "ViewModel: getOwajs() flow started")
                    _listState.value = OwajListState.Loading 
                }
                .catch { e -> 
                    Log.e("FLOW", "ViewModel: Error loading owajs", e)
                    _listState.value = OwajListState.Error(e.message ?: "Unknown error") 
                }
                .collect { owajs ->
                    Log.d("FLOW", "ViewModel: Received ${owajs.size} owajs")
                    if (owajs.isEmpty()) {
                        if (_listState.value !is OwajListState.Loading) {
                            _listState.value = OwajListState.Empty
                        }
                    } else {
                        _listState.value = OwajListState.Success(owajs)
                    }
                }
        }
    }

    fun loadOwajById(id: String) {
        Log.d("FLOW", "ViewModel: loadOwajById(id: $id) called")
        viewModelScope.launch {
            repository.getOwajById(id)
                .onStart { _detailState.value = OwajDetailState.Loading }
                .catch { e -> 
                    Log.e("FLOW", "ViewModel: Error loading owaj $id", e)
                    _detailState.value = OwajDetailState.Error(e.message ?: "Unknown error") 
                }
                .collect { owaj ->
                    Log.d("FLOW", "ViewModel: Received owaj detail: ${owaj?.title}")
                    if (owaj != null) {
                        _detailState.value = OwajDetailState.Success(owaj)
                    } else {
                        _detailState.value = OwajDetailState.Error("ওয়াজ পাওয়া যায়নি")
                    }
                }
        }
    }
}
