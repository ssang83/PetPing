package ai.comake.petping.data.repository

import ai.comake.petping.AppConstants
import ai.comake.petping.BuildConfig
import ai.comake.petping.api.WebService
import ai.comake.petping.data.repository.base.BaseRepository
import android.os.Build
import okhttp3.RequestBody
import java.util.*
import javax.inject.Inject

/**
 * android-petping-2
 * Class: LoginRepository
 * Created by cliff on 2022/03/10.
 *
 * Description:
 */
class LoginRepository @Inject constructor(private val webService: WebService) : BaseRepository() {

    suspend fun requestSignIn(sapaKey:String, body: RequestBody) = safeApiCall {
        webService.requestSignIn(sapaKey, body)
    }

    suspend fun requestSignInV2(sapaKey:String, body: RequestBody) = safeApiCall {
        webService.requestSignInV2(sapaKey, body)
    }

    suspend fun getPolicies(sapaKey: String) = safeApiCall {
        webService.getPolicies(sapaKey)
    }

    suspend fun requestSignUp(sapaKey: String, body: RequestBody) = safeApiCall {
        webService.requestSignUpSapa(
            sapaKey,
            BuildConfig.VERSION_NAME,
            Build.VERSION.RELEASE,
            Build.MODEL,
            "temp",
            UUID.randomUUID().toString(),
            body
        )
    }

    suspend fun requestSignUpV2(sapaKey: String, body: RequestBody) = safeApiCall {
        webService.requestSignUpSapaV2(
            sapaKey,
            BuildConfig.VERSION_NAME,
            Build.VERSION.RELEASE,
            Build.MODEL,
            "temp",
            UUID.randomUUID().toString(),
            body
        )
    }

    suspend fun findPassword(sapaKey: String, body: RequestBody) = safeApiCall {
        webService.findPassword(sapaKey, body)
    }

    suspend fun isDuplicationEmail(sapaKey: String, email:String) = safeApiCall {
        webService.requestHasDuplicateEmail(sapaKey, email)
    }
}