package nl.inholland.group9.karmakebab.data.models.auth

data class LoginResponse(
    val access_token: String,
    val expires_in: Int,
    val refresh_expires_in: Int,
    val refresh_token: String,
    val token_type: String,
    val id_token: String,
    val not_before_policy: Int,
    val session_state: String,
    val scope: String
)