package com.example.recitation_app.domain.repository

import com.example.recitation_app.domain.model.Zikir
import kotlinx.coroutines.flow.Flow

interface ZikirRepository {
    fun getZikirs(): Flow<List<Zikir>>
    fun getZikirById(id: String): Flow<Zikir?>
}