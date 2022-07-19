package ai.comake.petping.data.repository

import ai.comake.petping.api.WebService
import ai.comake.petping.data.repository.base.BaseRepository
import ai.comake.petping.data.vo.WalkFinishRequest
import ai.comake.petping.data.vo.WalkStartRequest
import okhttp3.ResponseBody
import javax.inject.Inject

class WalkRepository @Inject constructor(private val webService: WebService) : BaseRepository() {
    suspend fun walkablePetList(authKey: String, userId: String) = safeApiCall {
        webService.walkablePetList(authKey, userId)
    }

    suspend fun startWalk(authKey: String, body: WalkStartRequest) = safeApiCall {
        webService.startWalk(authKey, body)
    }

    suspend fun finishWalk(authKey: String, walkId: Int, body: WalkFinishRequest) = safeApiCall {
        webService.finishWalk(authKey, walkId, body)
    }

    suspend fun placePoiList(authKey: String, lat: String, lng: String) = safeApiCall {
        webService.placePoiList(authKey, lat, lng)
    }

    suspend fun markingPoiList(authKey: String, lat: String, lng: String, zoomLevel: Int) =
        safeApiCall {
            webService.markingPoiList(authKey, lat, lng, zoomLevel)
        }

    suspend fun markingDetail(authKey: String, id: Int) = safeApiCall {
        webService.markingDetail(authKey, id)
    }

    suspend fun placeDetail(authKey: String, id: Int) = safeApiCall {
        webService.placeDetail(authKey, id)
    }

    suspend fun getWalkingRecords(
        authKey: String,
        petId: Int,
        memberId: String,
        startIndex: Int,
        viewMode: String
    ) = safeApiCall {
        webService.getWalkingRecords(authKey, petId, memberId, startIndex, viewMode)
    }

    suspend fun deleteWalkRecord(authKey: String, walkId: Int) = safeApiCall {
        webService.deleteWalkRecord(authKey, walkId)
    }

    suspend fun getWalkStats(authKey: String, petId: Int) = safeApiCall {
        webService.getWalkStats(authKey, petId)
    }

    suspend fun walkAudioGuide(authKey: String, pageNo: Int) = safeApiCall {
        webService.walkAudioGuide(authKey, pageNo)
    }

    suspend fun downLoadFileUrl(url: String): ResponseBody =
        webService.getDownLoadFileUrl(url)
}