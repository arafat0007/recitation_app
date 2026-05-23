package com.example.recitation_app.data.repository

import com.example.recitation_app.domain.model.Owaj
import com.example.recitation_app.domain.repository.OwajRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MockOwajRepository : OwajRepository {
    private val mockOwajs = listOf(
        Owaj(
            id = "document_id_1",
            title = "মায়ের গুরুত্ব (The Importance of Mother)",
            speakerName = "মাওলানা মিজানুর রহমান আজহারী",
            description = "মায়ের মর্যাদা ও গুরুত্ব নিয়ে একটি সুন্দর ওয়াজ।",
            youtubeVideoId = "dQw4w9WgXcQ", // Replace with real ID later
            thumbnailUrl = "https://img.youtube.com/vi/dQw4w9WgXcQ/hqdefault.jpg",
            durationText = "১০:২৫",
            category = "ওয়াজ"
        ),
        Owaj(
            id = "document_id_2",
            title = "জান্নাতের বর্ণনা (Description of Paradise)",
            speakerName = "শায়খ আহমাদুল্লাহ",
            description = "জান্নাত ও এর নেয়ামতসমূহ নিয়ে বিস্তারিত আলোচনা।",
            youtubeVideoId = "dQw4w9WgXcQ",
            thumbnailUrl = "https://img.youtube.com/vi/dQw4w9WgXcQ/hqdefault.jpg",
            durationText = "১৫:৪০",
            category = "ওয়াজ"
        ),
        Owaj(
            id = "document_id_3",
            title = "ধৈর্যের ফজিলত (The Virtue of Patience)",
            speakerName = "মাওলানা তারিক জামিল",
            description = "বিপদে ধৈর্য ধারণের গুরুত্ব ও ফজিলত।",
            youtubeVideoId = "dQw4w9WgXcQ",
            thumbnailUrl = "https://img.youtube.com/vi/dQw4w9WgXcQ/hqdefault.jpg",
            durationText = "১২:৩০",
            category = "ওয়াজ"
        )
    )

    override fun getOwajs(): Flow<List<Owaj>> = flow {
        emit(emptyList()) // Simulate loading start
        delay(1000) // Simulate network delay
        emit(mockOwajs)
    }

    override fun getOwajById(id: String): Flow<Owaj?> = flow {
        delay(500)
        emit(mockOwajs.find { it.id == id })
    }
}
