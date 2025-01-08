package nl.inholland.group9.karmakebab.data.repositories

import kotlinx.coroutines.flow.first
import nl.inholland.group9.karmakebab.data.models.auth.LoginResponse
import nl.inholland.group9.karmakebab.data.models.auth.UserInfoResponse
import nl.inholland.group9.karmakebab.data.services.AuthService
import nl.inholland.group9.karmakebab.data.utils.TokenManager
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authService: AuthService,
    private val tokenManager: TokenManager
) {
    private var accessTokenExpirationTime: Instant? = null
    private var refreshTokenExpirationTime: Instant? = null

    suspend fun login(username: String, password: String): Result<LoginResponse> {
        return try {
            val response = authService.login(
                username = username,
                password = password,
                grant_type = "password",
                audience = "karma-kebab-client",
                scope = "openid profile email"
            )
            if (response.isSuccessful) {
                response.body()?.let {
                    saveTokens(it.access_token, it.refresh_token, it.expires_in, it.refresh_expires_in)
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Login failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveTokens(accessToken: String, refreshToken: String, expiresIn: Int, refreshExpiresIn: Int) {
        val currentTime = Instant.now()
        accessTokenExpirationTime = currentTime.plus(expiresIn.toLong(), ChronoUnit.SECONDS)
        refreshTokenExpirationTime = currentTime.plus(refreshExpiresIn.toLong(), ChronoUnit.SECONDS)
        tokenManager.saveTokens(accessToken, refreshToken)
    }

    suspend fun getToken(): String? {
        val currentTime = Instant.now()

        // Check if access token is valid
        if (accessTokenExpirationTime != null && currentTime.isBefore(accessTokenExpirationTime)) {
            return tokenManager.accessToken.first() // Return valid access token
        }

        // If access token is expired, try to refresh it
        if (refreshTokenExpirationTime != null && currentTime.isBefore(refreshTokenExpirationTime)) {
            return try {
                refreshToken() // Attempt to refresh the access token
            } catch (e: Exception) {
                null // If refreshing fails, clear tokens
            }
        }

        // If both tokens are invalid, clear tokens
        clearTokens()
        return null // Force re-login
    }

    suspend fun refreshToken(): String? {
        val refreshToken = tokenManager.refreshToken.first()

        // Check if the refresh token is expired
        if (refreshTokenExpirationTime != null && Instant.now().isAfter(refreshTokenExpirationTime)) {
            throw Exception("Refresh token expired. Please log in again.")
        }

        // Refresh the token using the AuthService
        val response = authService.refreshAccessToken(
            grant_type = "refresh_token",
            refresh_token = refreshToken ?: "",
            client_secret = "karma-kebab-client-secret",
            client_id = "karma-kebab-client"
        )

        return if (response.isSuccessful) {
            response.body()?.let {
                saveTokens(it.access_token, it.refresh_token, it.expires_in, it.refresh_expires_in)
                it.access_token
            }
        } else {
            throw Exception("Failed to refresh token: ${response.message()}")
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

    suspend fun clearTokens() {
        tokenManager.clearTokens()
    }

    fun getAccessTokenExpiration(): Instant? = accessTokenExpirationTime
    fun getRefreshTokenExpiration(): Instant? = refreshTokenExpirationTime
}