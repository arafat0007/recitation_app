package com.example.recitation_app.data.repository

import com.example.recitation_app.data.remote.firestore.FirestoreZikirDataSource
import com.example.recitation_app.domain.model.Zikir
import com.example.recitation_app.domain.repository.ZikirRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class ZikirRepositoryImpl(
    private val dataSource: FirestoreZikirDataSource = FirestoreZikirDataSource()
) : ZikirRepository {

    override fun getZikirs(): Flow<List<Zikir>> {
        return dataSource.getZikirs()
            .map { dtos -> dtos.map { it.toDomain() } }
            .catch { emit(emptyList()) }
    }

    override fun getZikirById(id: String): Flow<Zikir?> {
        return dataSource.getZikirById(id)
            .map { it?.toDomain() }
            .catch { emit(null) }
    }
}