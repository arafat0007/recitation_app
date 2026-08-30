package com.example.recitation_app.data.remote.firestore

import com.example.recitation_app.domain.model.Zikir
import com.google.firebase.firestore.PropertyName

data class ZikirDto(
    var id: String = "",
    var title: String = "",
    var url: String = "",
    var time: String = "",
    @get:PropertyName("file_name") @set:PropertyName("file_name") var fileName: String = ""
) {
    fun toDomain(): Zikir {
        return Zikir(
            id = id,
            title = title,
            url = url,
            time = time,
            fileName = fileName
        )
    }
}