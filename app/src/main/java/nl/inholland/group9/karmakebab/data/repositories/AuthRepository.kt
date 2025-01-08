package nl.inholland.group9.karmakebab.data.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import nl.inholland.group9.karmakebab.data.models.auth.UserInfoResponse
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    suspend fun login(username: String, password: String): Result<AuthResult> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(username, password).await()
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(username: String, password: String): Result<AuthResult> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(username, password).await()
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        firebaseAuth.signOut()
    }

    fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    fun getCurrentUser() = firebaseAuth.currentUser

    suspend fun getUserData(): UserInfoResponse? {
        val userId = firebaseAuth.currentUser?.uid
        return try {
            if (userId != null) {
                val snapshot = firestore.collection("users").document(userId).get().await()
                snapshot.toObject(UserInfoResponse::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
