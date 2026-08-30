package com.example.recitation_app.data.remote.firestore

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class FirestoreZikirDataSource(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    fun getZikirs(): Flow<List<ZikirDto>> = callbackFlow {
        val subscription = firestore.collection("zikirs")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val zikirs = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(ZikirDto::class.java)?.apply {
                            id = doc.id
                        }
                    }
                    trySend(zikirs)
                }
            }
        awaitClose { subscription.remove() }
    }

    fun getZikirById(id: String): Flow<ZikirDto?> = callbackFlow {
        val docRef = firestore.collection("zikirs").document(id)
        val subscription = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val zikir = snapshot.toObject(ZikirDto::class.java)?.apply {
                    this.id = snapshot.id
                }
                trySend(zikir)
            } else {
                trySend(null)
            }
        }
        awaitClose { subscription.remove() }
    }
}