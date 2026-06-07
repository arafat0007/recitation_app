package com.example.recitation_app.data.repository

import com.example.recitation_app.data.remote.firestore.FirestoreOwajDataSource
import com.example.recitation_app.domain.model.Owaj
import com.example.recitation_app.domain.repository.OwajRepository
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OwajRepositoryImpl(
    private val remoteDataSource: FirestoreOwajDataSource = FirestoreOwajDataSource()
) : OwajRepository {

    override fun getPaginatedOwajs(
        pageSize: Long,
        startAfter: DocumentSnapshot?,
        endBefore: DocumentSnapshot?
    ): Flow<OwajRepository.PaginatedOwajs> {
        return remoteDataSource.getPaginatedOwajs(pageSize, startAfter, endBefore).map { paginated ->
            OwajRepository.PaginatedOwajs(
                owajs = paginated.owajs.map { it.toDomain() },
                firstDoc = paginated.firstDoc,
                lastDoc = paginated.lastDoc
            )
        }
    }

    override fun getOwajById(id: String): Flow<Owaj?> {
        return remoteDataSource.getOwajById(id).map { it?.toDomain() }
    }

    override fun getWatchedVideoIds(userId: String): Flow<List<String>> {
        return remoteDataSource.getWatchedVideoIds(userId)
    }

    override suspend fun toggleWatchedStatus(userId: String, owajId: String, isWatched: Boolean) {
        remoteDataSource.toggleWatchedStatus(userId, owajId, isWatched)
    }

    override fun getWatchedStatus(userId: String, owajId: String): Flow<Boolean> {
        return remoteDataSource.getWatchedStatus(userId, owajId)
    }
}
