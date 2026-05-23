package com.example.recitation_app.data.remote.firestore

import com.example.recitation_app.domain.model.Owaj
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName

data class OwajDto(
    @get:Exclude @set:Exclude var id: String = "",
    @get:PropertyName("title") @set:PropertyName("title") var title: String = "",
    @get:PropertyName("speakerName") @set:PropertyName("speakerName") var speakerName: String = "",
    @get:PropertyName("description") @set:PropertyName("description") var description: String = "",
    @get:PropertyName("youtubeVideoId") @set:PropertyName("youtubeVideoId") var youtubeVideoId: String = "",
    @get:PropertyName("thumbnailUrl") @set:PropertyName("thumbnailUrl") var thumbnailUrl: String = "",
    @get:PropertyName("durationText") @set:PropertyName("durationText") var durationText: String? = null,
    @get:PropertyName("category") @set:PropertyName("category") var category: String? = null,
    @get:PropertyName("sortOrder") @set:PropertyName("sortOrder") var sortOrder: Int = 0,
    @get:PropertyName("isPublished") @set:PropertyName("isPublished") var isPublished: Boolean = true,
    @get:PropertyName("updatedAt") @set:PropertyName("updatedAt") var updatedAt: Long = System.currentTimeMillis()
) {
    fun toDomain(): Owaj {
        return Owaj(
            id = id,
            title = title,
            speakerName = speakerName,
            description = description,
            youtubeVideoId = youtubeVideoId,
            thumbnailUrl = thumbnailUrl,
            durationText = durationText,
            category = category,
            sortOrder = sortOrder,
            isPublished = isPublished,
            updatedAt = updatedAt
        )
    }
}
