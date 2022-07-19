package ai.comake.petping.data.vo

data class LoginResponse(
    val authorizationToken: String,
    val email: String,
    val id: String,
    val isEmailAuthSend: Boolean,
    val name: String,
    val tokenExpireAt: String
)