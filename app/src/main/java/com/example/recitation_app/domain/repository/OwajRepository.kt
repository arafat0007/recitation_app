package com.example.recitation_app.domain.repository

import com.example.recitation_app.domain.model.Owaj
import kotlinx.coroutines.flow.Flow

interface OwajRepository {
    fun getOwajs(): Flow<List<Owaj>>
    fun getOwajById(id: String): Flow<Owaj?>
}
