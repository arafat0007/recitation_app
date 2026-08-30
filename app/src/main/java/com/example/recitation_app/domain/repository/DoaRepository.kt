package com.example.recitation_app.domain.repository

import com.example.recitation_app.domain.model.PostSalahAdhkar
import com.example.recitation_app.domain.model.Salah
import kotlinx.coroutines.flow.Flow

interface DoaRepository {
    fun getPostSalahAdhkar(salah: Salah): Flow<List<PostSalahAdhkar>>
    fun getAdhkarById(id: String): Flow<PostSalahAdhkar?>
}
