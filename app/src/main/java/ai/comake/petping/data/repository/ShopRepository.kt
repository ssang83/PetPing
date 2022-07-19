package ai.comake.petping.data.repository

import ai.comake.petping.api.WebService
import ai.comake.petping.data.repository.base.BaseRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

/**
 * android-petping-2
 * Class: ShopRepository
 * Created by cliff on 2022/02/10.
 *
 * Description:
 */
class ShopRepository @Inject constructor(private val webService: WebService) : BaseRepository() {

    suspend fun getShopRecommendList(authKey: String, memberId:String) = safeApiCall {
        webService.getShopItemsList(authKey, memberId)
    }

    suspend fun shopSignUp(authKey: String, memberId: String) = safeApiCall {
        webService.shopSignUp(authKey, memberId)
    }

    suspend fun getPingPoint(authKey: String, memberId: String) = safeApiCall {
        webService.getPingAmount(authKey, memberId)
    }

    suspend fun shopLogin(header:String, flag:String, mode:String, body: RequestBody) = safeApiCall {
        webService.shopLogin(header, flag, mode, body)
    }

    suspend fun getDetailPing(authKey: String, memberId: String) = safeApiCall {
        webService.getDetailPings(authKey, memberId)
    }

    suspend fun getAvailablePing(authKey: String, memberId: String) = safeApiCall {
        webService.getAvailablePings(authKey, memberId)
    }

    suspend fun getSavingPing(authKey: String, memberId: String, pageNo:Number) = safeApiCall {
        webService.getSavingPings(authKey, memberId, pageNo, 1)
    }

    suspend fun getExpirePing(authKey: String, memberId: String, pageNo:Number) = safeApiCall {
        webService.getExpiredPings(authKey, memberId, pageNo)
    }

    suspend fun getUsingPing(authKey: String, memberId: String, pageNo:Number) = safeApiCall {
        webService.getUsingPings(authKey, memberId, pageNo, 2)
    }

    suspend fun getOngoingMission(authKey: String, memberId: String, pageNo:Number) = safeApiCall {
        webService.getOngoingMissions(authKey, memberId, pageNo)
    }

    suspend fun getCompleteMission(authKey: String, memberId: String, pageNo:Number) = safeApiCall {
        webService.getCompletionMissions(authKey, memberId, pageNo)
    }

    suspend fun uploadMissionFiles(
        authKey: String,
        memberId: MultipartBody.Part,
        missionId: MultipartBody.Part,
        body: List<MultipartBody.Part>
    ) = safeApiCall {
        webService.uploadMissionFiles(authKey, memberId, missionId, body)
    }
}