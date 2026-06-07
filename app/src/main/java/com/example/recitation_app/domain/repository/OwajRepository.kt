package com.example.recitation_app.domain.repository

import com.example.recitation_app.domain.model.Owaj
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.Flow

interface OwajRepository {
    data class PaginatedOwajs(
        val owajs: List<Owaj>,
        val firstDoc: DocumentSnapshot?,
        val lastDoc: DocumentSnapshot?
    )

    fun getPaginatedOwajs(
        pageSize: Long = 20,
        startAfter: DocumentSnapshot? = null,
        endBefore: DocumentSnapshot? = null
    ): Flow<PaginatedOwajs>

    fun getOwajById(id: String): Flow<Owaj?>
    
    fun getWatchedVideoIds(userId: String): Flow<List<String>>
    
    suspend fun toggleWatchedStatus(userId: String, owajId: String, isWatched: Boolean)
    
    fun getWatchedStatus(userId: String, owajId: String): Flow<Boolean>
}
