package com.example.recitation_app.feature_owaj.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recitation_app.data.repository.AuthRepositoryImpl
import com.example.recitation_app.data.repository.OwajRepositoryImpl
import com.example.recitation_app.domain.model.Owaj
import com.example.recitation_app.domain.repository.AuthRepository
import com.example.recitation_app.domain.repository.OwajRepository
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class OwajListState {
    object Selection : OwajListState()
    object Loading : OwajListState()
    data class Success(
        val owajs: List<Owaj>,
        val hasPrevious: Boolean,
        val hasNext: Boolean
    ) : OwajListState()
    data class Error(val message: String) : OwajListState()
    object Empty : OwajListState()
}

sealed class OwajDetailState {
    object Loading : OwajDetailState()
    data class Success(val owaj: Owaj, val isWatched: Boolean) : OwajDetailState()
    data class Error(val message: String) : OwajDetailState()
}

class OwajViewModel(
    private val repository: OwajRepository = OwajRepositoryImpl(),
    private val authRepository: AuthRepository = AuthRepositoryImpl()
) : ViewModel() {

    private val _listState = MutableStateFlow<OwajListState>(OwajListState.Selection)
    val listState: StateFlow<OwajListState> = _listState.asStateFlow()

    private val _detailState = MutableStateFlow<OwajDetailState>(OwajDetailState.Loading)
    val detailState: StateFlow<OwajDetailState> = _detailState.asStateFlow()

    private var firstDoc: DocumentSnapshot? = null
    private var lastDoc: DocumentSnapshot? = null
    private var filterWatched: Boolean? = null
    private var currentPageIndex = 0
    
    private val watchedIds = authRepository.currentUserId?.let { 
        repository.getWatchedVideoIds(it) 
    } ?: flowOf(emptyList())

    fun showSelection() {
        _listState.value = OwajListState.Selection
    }

    fun loadOwajs(watched: Boolean) {
        filterWatched = watched
        firstDoc = null
        lastDoc = null
        currentPageIndex = 0
        fetchPage()
    }

    fun nextPage() {
        currentPageIndex++
        fetchPage(startAfter = lastDoc)
    }

    fun previousPage() {
        currentPageIndex--
        fetchPage(endBefore = firstDoc)
    }

    private fun fetchPage(startAfter: DocumentSnapshot? = null, endBefore: DocumentSnapshot? = null) {
        val userId = authRepository.currentUserId ?: return
        
        viewModelScope.launch {
            _listState.value = OwajListState.Loading
            
            combine(
                repository.getPaginatedOwajs(20, startAfter, endBefore),
                watchedIds
            ) { paginated, watched ->
                val filtered = if (filterWatched == true) {
                    paginated.owajs.filter { watched.contains(it.id) }
                } else {
                    paginated.owajs.filter { !watched.contains(it.id) }
                }
                
                Triple(filtered, paginated.firstDoc, paginated.lastDoc)
            }
            .catch { e -> _listState.value = OwajListState.Error(e.message ?: "Error") }
            .collect { (owajs, first, last) ->
                firstDoc = first
                lastDoc = last
                
                if (owajs.isEmpty() && startAfter == null && endBefore == null) {
                    _listState.value = OwajListState.Empty
                } else {
                    _listState.value = OwajListState.Success(
                        owajs = owajs,
                        hasPrevious = currentPageIndex > 0,
                        hasNext = owajs.size >= 20 
                    )
                }
            }
        }
    }

    fun loadOwajById(id: String) {
        val userId = authRepository.currentUserId ?: return
        viewModelScope.launch {
            _detailState.value = OwajDetailState.Loading
            combine(
                repository.getOwajById(id),
                repository.getWatchedStatus(userId, id)
            ) { owaj, isWatched ->
                if (owaj != null) {
                    OwajDetailState.Success(owaj, isWatched)
                } else {
                    OwajDetailState.Error("ওয়াজ পাওয়া যায়নি")
                }
            }
            .catch { e -> _detailState.value = OwajDetailState.Error(e.message ?: "Error") }
            .collect { _detailState.value = it }
        }
    }

    fun toggleWatched(owajId: String, isWatched: Boolean) {
        val userId = authRepository.currentUserId ?: return
        viewModelScope.launch {
            repository.toggleWatchedStatus(userId, owajId, isWatched)
        }
    }
}
