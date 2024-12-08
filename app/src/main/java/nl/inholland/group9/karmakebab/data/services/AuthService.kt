package nl.inholland.group9.karmakebab.data.services

import nl.inholland.group9.karmakebab.data.models.auth.LoginResponse
import nl.inholland.group9.karmakebab.data.models.auth.UserInfoResponse
import retrofit2.Response
import retrofit2.http.*

interface AuthService {
    @FormUrlEncoded
    @POST("/auth/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<LoginResponse>

    @GET("/auth/userinfo")
    suspend fun getUserInfo(
    ): Response<UserInfoResponse>

    @FormUrlEncoded
    @POST("/auth/refresh")
    suspend fun refreshAccessToken(
        @Field("refresh_token") refreshToken: String
    ): Response<LoginResponse>
}