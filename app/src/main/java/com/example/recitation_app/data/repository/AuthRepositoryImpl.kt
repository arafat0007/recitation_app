package com.example.recitation_app.data.repository

import com.example.recitation_app.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : AuthRepository {

    private val auth: FirebaseAuth
        get() = FirebaseAuth.getInstance()

    private val _isLoggedIn = MutableStateFlow(auth.currentUser != null)
    override val isLoggedIn: Flow<Boolean> = _isLoggedIn.asStateFlow()

    override val currentUserEmail: String?
        get() = auth.currentUser?.email

    override val currentUserId: String?
        get() = auth.currentUser?.uid

    override suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            _isLoggedIn.value = true
            Result.success(Unit)
        } catch (e: Exception) {
            var message = "লগইন করতে সমস্যা হয়েছে। দয়া করে আবার চেষ্টা করুন।"
            
            try {
                // If login fails, double check if the email actually exists
                // This helps when Email Enumeration Protection is enabled in Firebase
                val methods = auth.fetchSignInMethodsForEmail(email).await().signInMethods ?: emptyList<String>()
                
                if (methods.isEmpty()) {
                    message = "এই ইমেইলে কোনো অ্যাকাউন্ট নেই। দয়া করে নতুন অ্যাকাউন্ট তৈরি করুন।"
                } else {
                    message = "ভুল পাসওয়ার্ড। আবার চেষ্টা করুন।"
                }
            } catch (checkError: Exception) {
                // Fallback to basic exception mapping if the check fails
                message = when (e) {
                    is com.google.firebase.auth.FirebaseAuthInvalidUserException -> 
                        "এই ইমেইলে কোনো অ্যাকাউন্ট নেই। দয়া করে নতুন অ্যাকাউন্ট তৈরি করুন।"
                    is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException -> 
                        "ভুল পাসওয়ার্ড। আবার চেষ্টা করুন।"
                    else -> "লগইন করতে সমস্যা হয়েছে। দয়া করে আবার চেষ্টা করুন।"
                }
            }
            Result.failure(Exception(message))
        }
    }

    override suspend fun register(name: String, email: String, password: String): Result<Unit> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: throw Exception("Registration failed")
            
            // Store user details in Firestore
            val userMap = mapOf(
                "name" to name,
                "email" to email,
                "createdAt" to com.google.firebase.Timestamp.now()
            )
            firestore.collection("users").document(uid).set(userMap).await()
            
            _isLoggedIn.value = true
            Result.success(Unit)
        } catch (e: Exception) {
            val message = when (e) {
                is com.google.firebase.auth.FirebaseAuthUserCollisionException -> 
                    "এই ইমেইলে ইতিপূর্বেই অ্যাকাউন্ট তৈরি করা হয়েছে। লগইন করার চেষ্টা করুন।"
                is com.google.firebase.auth.FirebaseAuthWeakPasswordException -> 
                    "পাসওয়ার্ডটি অন্তত ৬ অক্ষরের হতে হবে।"
                else -> "অ্যাকাউন্ট তৈরি করতে সমস্যা হয়েছে। আবার চেষ্টা করুন।"
            }
            Result.failure(Exception(message))
        }
    }

    override suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        auth.signOut()
        _isLoggedIn.value = false
    }
}
