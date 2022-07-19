package ai.comake.petping.data.vo

/**
 * android-petping-2
 * Class: AccessTokenResponse
 * Created by cliff on 2022/02/09.
 *
 * Description:
 */
data class AccessTokenResponse(
    val authorizationToken: String,
    val memberID: String,
    val tokenExpireAt: String
)
