package nl.inholland.group9.karmakebab.data.models.auth

data class UserInfoResponse(
    val sub: String,
    val email_verified: Boolean,
    val name: String,
    val preferred_username: String,
    val given_name: String,
    val family_name: String,
    val email: String
)