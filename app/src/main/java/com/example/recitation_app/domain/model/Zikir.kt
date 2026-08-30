package com.example.recitation_app.domain.model

data class Zikir(
    val id: String,
    val title: String,
    val url: String,
    val time: String,
    val fileName: String = ""
)