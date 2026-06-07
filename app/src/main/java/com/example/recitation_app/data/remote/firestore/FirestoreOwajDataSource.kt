package com.example.recitation_app.data.remote.firestore

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreOwajDataSource(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    data class PaginatedOwajs(
        val owajs: List<OwajDto>,
        val firstDoc: DocumentSnapshot?,
        val lastDoc: DocumentSnapshot?
    )

    fun getPaginatedOwajs(
        pageSize: Long = 20,
        startAfter: DocumentSnapshot? = null,
        endBefore: DocumentSnapshot? = null
    ): Flow<PaginatedOwajs> = callbackFlow {
        var query = firestore.collection("owajs")
            .whereEqualTo("isPublished", true)
            .orderBy("sortOrder", Query.Direction.ASCENDING)

        if (startAfter != null) {
            query = query.startAfter(startAfter).limit(pageSize)
        } else if (endBefore != null) {
            query = query.endBefore(endBefore).limitToLast(pageSize)
        } else {
            query = query.limit(pageSize)
        }

        val subscription = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val owajs = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(OwajDto::class.java)?.apply {
                        id = doc.id
                    }
                }
                val first = snapshot.documents.firstOrNull()
                val last = snapshot.documents.lastOrNull()
                trySend(PaginatedOwajs(owajs, first, last))
            }
        }

        awaitClose { subscription.remove() }
    }

    suspend fun toggleWatchedStatus(userId: String, owajId: String, isWatched: Boolean) {
        val docId = "${userId}_${owajId}"
        val data = mapOf(
            "userId" to userId,
            "owajId" to owajId,
            "isWatched" to isWatched,
            "updatedAt" to com.google.firebase.Timestamp.now()
        )
        firestore.collection("user_video_status").document(docId).set(data).await()
    }

    fun getWatchedVideoIds(userId: String): Flow<List<String>> = callbackFlow {
        val query = firestore.collection("user_video_status")
            .whereEqualTo("userId", userId)
            .whereEqualTo("isWatched", true)

        val subscription = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val ids = snapshot.documents.mapNotNull { it.getString("owajId") }
                trySend(ids)
            }
        }
        awaitClose { subscription.remove() }
    }

    fun getOwajById(id: String): Flow<OwajDto?> = callbackFlow {
        val docRef = firestore.collection("owajs").document(id)
        val subscription = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val owaj = snapshot.toObject(OwajDto::class.java)?.apply {
                    this.id = snapshot.id
                }
                trySend(owaj)
            } else {
                trySend(null)
            }
        }
        awaitClose { subscription.remove() }
    }
    
    fun getWatchedStatus(userId: String, owajId: String): Flow<Boolean> = callbackFlow {
        val docId = "${userId}_${owajId}"
        val subscription = firestore.collection("user_video_status").document(docId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val isWatched = snapshot?.getBoolean("isWatched") ?: false
                trySend(isWatched)
            }
        awaitClose { subscription.remove() }
    }
}
