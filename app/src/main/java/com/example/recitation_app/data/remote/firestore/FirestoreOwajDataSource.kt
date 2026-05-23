package com.example.recitation_app.data.remote.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class FirestoreOwajDataSource(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    fun getPublishedOwajs(): Flow<List<OwajDto>> = callbackFlow {
        val query = firestore.collection("owajs")
            .whereEqualTo("isPublished", true)
            .orderBy("sortOrder", Query.Direction.ASCENDING)

        val subscription = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val owajs = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(OwajDto::class.java)?.apply {
                        id = doc.id // Force use of Firestore Document ID
                    }
                }
                trySend(owajs)
            }
        }

        awaitClose { subscription.remove() }
    }

    fun getOwajById(id: String): Flow<OwajDto?> = callbackFlow {
        android.util.Log.d("FLOW", "FirestoreOwajDataSource: Fetching document with ID: $id")
        val docRef = firestore.collection("owajs").document(id)
        
        val subscription = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                android.util.Log.e("FLOW", "FirestoreOwajDataSource: Error fetching document $id", error)
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                android.util.Log.d("FLOW", "FirestoreOwajDataSource: Document found! Mapping data for $id")
                val owaj = snapshot.toObject(OwajDto::class.java)?.apply {
                    this.id = snapshot.id // Force use of Firestore Document ID
                }
                trySend(owaj)
            } else {
                android.util.Log.w("FLOW", "FirestoreOwajDataSource: Document NOT found or DOES NOT EXIST for ID: $id")
                trySend(null)
            }
        }

        awaitClose { subscription.remove() }
    }
}
