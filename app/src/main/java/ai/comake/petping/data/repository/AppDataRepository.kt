package ai.comake.petping.data.repository

import ai.comake.petping.api.WebService
import ai.comake.petping.data.repository.base.BaseRepository
import okhttp3.RequestBody
import javax.inject.Inject

class AppDataRepository @Inject constructor(private val webService: WebService) : BaseRepository() {
    suspend fun appVersion(authKey: String) = safeApiCall {
        webService.appVersion(authKey)
    }

    suspend fun getAccessTokenTime(authKey: String) = safeApiCall {
        webService.getAccessTokenTime(authKey)
    }

    suspend fun getAppInfo(authKey: String, memberId: String) = safeApiCall {
        webService.getAppInfo(authKey, memberId)
    }

    suspend fun getNotificationStatus(authKey: String, memberId: String) = safeApiCall {
        webService.getNotificationStatus(authKey, memberId)
    }

    suspend fun setPushMarketingInfo(authKey: String, memberId: String, body: RequestBody) =
        safeApiCall {
            webService.setPushMarketingInfo(authKey, memberId, body)
        }

    suspend fun setNotificationStatus(authKey: String, type: String, body: RequestBody) =
        safeApiCall {
            webService.setNotificationStatus(authKey, type, body)
        }

    suspend fun getDashboard(
        authKey: String,
        appVersion: String,
        osVersion: String,
        deviceInfo: String,
        mobileCarrierInfo: String,
        deviceId: String,
        lat: String,
        lng: String
    ) = safeApiCall {
        webService.getDashboard(
            authKey,
            appVersion,
            osVersion,
            deviceInfo,
            mobileCarrierInfo,
            deviceId,
            lat,
            lng
        )
    }

    suspend fun getDashboard(
        authKey: String,
        appVersion: String,
        osVersion: String,
        deviceInfo: String,
        mobileCarrierInfo: String,
        deviceId: String,
        profileId: Int,
        lat: String,
        lng: String
    ) = safeApiCall {
        webService.getDashboard(
            authKey,
            appVersion,
            osVersion,
            deviceInfo,
            mobileCarrierInfo,
            deviceId,
            profileId,
            lat,
            lng
        )
    }

    suspend fun getPingTip(authKey: String) = safeApiCall {
        webService.getPingTip(authKey)
    }

    suspend fun getPingZoneFriends(authKey: String, petId: Int) = safeApiCall {
        webService.getPingZoneFriends(authKey, petId)
    }

    suspend fun getAppVersion(authKey: String) = safeApiCall {
        webService.getAppVersion(authKey)
    }

    suspend fun registFcmToken(authKey: String, requestBody: RequestBody) = safeApiCall {
        webService.registFcmToken(authKey, requestBody)
    }

    suspend fun getNewBadge(authKey: String, memberId: String) = safeApiCall {
        webService.requestNewBadge(authKey, memberId)
    }

    suspend fun getRefreshToken(authKey: String) = safeApiCall {
        webService.getNewToken(authKey)
    }
}