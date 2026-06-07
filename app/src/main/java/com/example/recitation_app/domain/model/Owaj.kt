package com.example.recitation_app.domain.model

data class Owaj(
    val id: String,
    val title: String,
    val speakerName: String,
    val description: String,
    val youtubeVideoId: String,
    val thumbnailUrl: String,
    val durationText: String? = null,
    val category: String? = null,
    val sortOrder: Int = 0,
    val isPublished: Boolean = true,
    val updatedAt: Long = System.currentTimeMillis(),
    val isWatched: Boolean = false
)
