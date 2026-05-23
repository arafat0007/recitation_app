package com.example.recitation_app.data.repository

import com.example.recitation_app.data.remote.firestore.FirestoreOwajDataSource
import com.example.recitation_app.domain.model.Owaj
import com.example.recitation_app.domain.repository.OwajRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OwajRepositoryImpl(
    private val remoteDataSource: FirestoreOwajDataSource = FirestoreOwajDataSource()
) : OwajRepository {

    override fun getOwajs(): Flow<List<Owaj>> {
        return remoteDataSource.getPublishedOwajs().map { dtos ->
            dtos.map { it.toDomain() }
        }
    }

    override fun getOwajById(id: String): Flow<Owaj?> {
        return remoteDataSource.getOwajById(id).map { it?.toDomain() }
    }
}
