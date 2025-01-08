package nl.inholland.group9.karmakebab.data.networks

import android.util.Log
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response
import java.util.Base64

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val updatedRequest = request.newBuilder()
            .header("Authorization", Credentials.basic("karma-kebab-client", "karma-kebab-client-secret"))
            .build()

        Log.d("AuthInterceptor", "Authorization Header: ${updatedRequest.header("Authorization")}")

        return chain.proceed(updatedRequest)
    }
}
