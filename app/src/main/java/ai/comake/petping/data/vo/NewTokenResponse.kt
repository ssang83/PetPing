package ai.comake.petping.data.vo

/**
 * android-petping-2
 * Class: NewTokenResponse
 * Created by cliff on 2022/08/16.
 *
 * Description:
 */
data class NewTokenResponse(
    val authorizationToken: String,
    val memberID: String,
    val tokenExpireAt: String
)