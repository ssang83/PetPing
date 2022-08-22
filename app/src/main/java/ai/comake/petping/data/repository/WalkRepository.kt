package ai.comake.petping.data.repository

import ai.comake.petping.api.WebService
import ai.comake.petping.data.repository.base.BaseRepository
import ai.comake.petping.data.vo.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import javax.inject.Inject

class WalkRepository @Inject constructor(private val webService: WebService) : BaseRepository() {
    suspend fun walkablePetList(authKey: String, userId: String) = safeApiCall {
        webService.walkablePetList(authKey, userId)
    }

    suspend fun startWalk(authKey: String, body: WalkStartRequest) = safeApiCall {
        webService.startWalk(authKey, body)
    }

    suspend fun finishWalk(authKey: String, walkId: Int, body: WalkFinishRequest) = safeApiCall {
        webService.finishWalkSapa(authKey, walkId, body)
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

    suspend fun audioGuideList(authKey: String, pageNo: Int) = safeApiCall {
        webService.audioGuideList(authKey, pageNo)
    }

    suspend fun audioGuideLog(
        authKey: String,
        body: AudioGuideLog
    ) = safeApiCall {
        webService.audioGuideLog(authKey, body)
    }

    suspend fun downLoadFileUrl(url: String): ResponseBody =
        webService.getDownLoadFileUrl(url)

    suspend fun walkFinishRecord(
        authKey: String,
        walkId: Int,
        review: MultipartBody.Part,
        file: List<MultipartBody.Part>
    ) = safeApiCall {
        webService.walkFinishRecord(authKey, walkId, review, file)
    }

    suspend fun registerMyMarking(authKey: String, walkId: Int, requestBody: MyMarkingPoi) =
        safeApiCall {
            webService.registerMyMarking(authKey, walkId, requestBody)
        }
}