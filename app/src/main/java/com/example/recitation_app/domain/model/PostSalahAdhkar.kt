package com.example.recitation_app.domain.model

enum class Salah(val displayName: String, val key: String) {
    FAJR("ফজর", "fajr"),
    DHUHR("যোহর", "dhuhr"),
    ASR("আসর", "asr"),
    MAGHRIB("মাগরিব", "maghrib"),
    ISHA("এশা", "isha");

    companion object {
        fun fromKey(key: String): Salah? = values().find { it.key == key }
    }
}

data class PostSalahAdhkar(
    val id: String,
    val title: String,
    val arabic: String,
    val pronunciation: String,
    val meaning: String,
    val audio: String,
    val counts: Map<String, Int>,
    val reference: String,
    val order: Int
)
