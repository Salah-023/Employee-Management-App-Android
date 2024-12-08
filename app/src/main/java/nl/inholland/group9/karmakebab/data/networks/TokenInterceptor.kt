package nl.inholland.group9.karmakebab.data.networks

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import nl.inholland.group9.karmakebab.data.repositories.AuthRepository
import nl.inholland.group9.karmakebab.data.utils.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class TokenInterceptor @Inject constructor(
    private val tokenManager: TokenManager,
    private val authRepository: AuthRepository
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val token = runBlocking { tokenManager.accessToken.first() }

        if (!token.isNullOrEmpty()) {
            request = request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        }

        val response = chain.proceed(request)

        // If the token is expired, try to refresh it
        return if (response.code == 401) {
            runBlocking {
                val refreshResult = authRepository.refreshAccessToken()
                if (refreshResult.isSuccess) {
                    val newToken = refreshResult.getOrNull()
                    if (!newToken.isNullOrEmpty()) {
                        val newRequest = request.newBuilder()
                            .removeHeader("Authorization")
                            .addHeader("Authorization", "Bearer $newToken")
                            .build()
                        return@runBlocking chain.proceed(newRequest)
                    }
                }
                response
            }
        } else {
            response
        }
    }
}