//package nl.inholland.group9.karmakebab.data.networks
//
//import kotlinx.coroutines.runBlocking
//import nl.inholland.group9.karmakebab.data.repositories.AuthRepository
//import okhttp3.Interceptor
//import okhttp3.Response
//import javax.inject.Inject
//
//
//class TokenInterceptor @Inject constructor(
//    private val authRepository: AuthRepository
//) : Interceptor {
//    override fun intercept(chain: Interceptor.Chain): Response {
//        val request = chain.request()
//
//        // Fetch a valid token using AuthRepository
//        val token = runBlocking { authRepository.getToken() }
//
//        // Add the token to the request if it exists
//        val newRequest = if (!token.isNullOrEmpty()) {
//            request.newBuilder()
//                .addHeader("Authorization", "Bearer $token")
//                .build()
//        } else {
//            request
//        }
//
//        return chain.proceed(newRequest)
//    }
//}