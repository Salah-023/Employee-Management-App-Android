package nl.inholland.group9.karmakebab.data.repositories

import nl.inholland.group9.karmakebab.data.models.auth.LoginResponse
import nl.inholland.group9.karmakebab.data.models.auth.UserInfoResponse
import nl.inholland.group9.karmakebab.data.services.AuthService
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authService: AuthService
) {
    suspend fun login(username: String, password: String): Result<LoginResponse> {
        return try {
            val response = authService.login(username, password)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Login failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserInfo(): Result<UserInfoResponse> {
        return try {
            val response = authService.getUserInfo()
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Failed to fetch user info"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}