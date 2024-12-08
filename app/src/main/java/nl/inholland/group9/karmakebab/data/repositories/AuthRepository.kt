package nl.inholland.group9.karmakebab.data.repositories

import kotlinx.coroutines.flow.first
import nl.inholland.group9.karmakebab.data.models.auth.LoginResponse
import nl.inholland.group9.karmakebab.data.models.auth.UserInfoResponse
import nl.inholland.group9.karmakebab.data.services.AuthService
import nl.inholland.group9.karmakebab.data.utils.TokenManager
import javax.inject.Inject

class AuthRepository @Inject constructor(private val authService: AuthService,
                                         private val tokenManager: TokenManager
) {

    suspend fun login(username: String, password: String): Result<LoginResponse> {
        return try {
            val response = authService.login(username, password)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty response body"))
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
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch user info"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun refreshAccessToken(): Result<String> {
        val refreshToken = tokenManager.refreshToken.first()
        if (refreshToken.isNullOrEmpty()) return Result.failure(Exception("Refresh token not available"))

        return try {
            val response = authService.refreshAccessToken(refreshToken)
            if (response.isSuccessful) {
                val newAccessToken = response.body()?.access_token ?: return Result.failure(Exception("Invalid response"))
                tokenManager.saveTokens(newAccessToken, refreshToken)
                Result.success(newAccessToken)
            } else {
                Result.failure(Exception("Failed to refresh token"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}