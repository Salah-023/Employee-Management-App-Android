package nl.inholland.group9.karmakebab.data.services

import nl.inholland.group9.karmakebab.data.models.auth.LoginResponse
import nl.inholland.group9.karmakebab.data.models.auth.UserInfoResponse
import retrofit2.Response
import retrofit2.http.*

interface AuthService {
    @FormUrlEncoded
    @POST("realms/karma-kebab-realm/protocol/openid-connect/token")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("grant_type") grant_type: String,
        @Field("audience") audience: String,
        @Field("scope") scope: String
    ): Response<LoginResponse>

    @GET("/auth/userinfo")
    suspend fun getUserInfo(
    ): Response<UserInfoResponse>

    @FormUrlEncoded
    @POST("realms/karma-kebab-realm/protocol/openid-connect/token")
    suspend fun refreshAccessToken(
        @Field("grant_type") grant_type: String,
        @Field("refresh_token") refresh_token: String,
        @Field("client_secret") client_secret: String,
        @Field("client_id") client_id: String

    ): Response<LoginResponse>
}