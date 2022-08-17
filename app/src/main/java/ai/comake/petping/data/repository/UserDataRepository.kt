package ai.comake.petping.data.repository

import ai.comake.petping.api.WebService
import ai.comake.petping.data.repository.base.BaseRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Part
import javax.inject.Inject

/**
 * android-petping-2
 * Class: UserDataRepository
 * Created by cliff on 2022/02/22.
 *
 * Description:
 */
class UserDataRepository @Inject constructor(private val webService: WebService) :
    BaseRepository() {

    suspend fun getMyPageInfo(authKey: String, memberId:String) = safeApiCall {
        webService.getMyPageInfo(authKey, memberId)
    }

    suspend fun getNoticeList(authKey: String) = safeApiCall {
        webService.getNoticeList(authKey)
    }

    suspend fun getMemberInfo(authKey: String, memeberId:String) = safeApiCall {
        webService.getMemberInfo(authKey, memeberId)
    }

    suspend fun sendEmailAuth(authKey: String, body: RequestBody) = safeApiCall {
        webService.sendAuthEmail(authKey, body)
    }

    suspend fun getFaqList(authKey: String) = safeApiCall {
        webService.getFaqList(authKey)
    }

    suspend fun getInquirys(authKey: String, memberId: String) = safeApiCall {
        webService.getInquirys(authKey, memberId)
    }

    suspend fun getWelcomeKitApply(authKey: String, memberId: String) = safeApiCall {
        webService.getWelcomeKitApply(authKey, memberId)
    }

    suspend fun requestHasDuplicateEmail(authKey: String, email:String) = safeApiCall {
        webService.requestHasDuplicateEmail(authKey, email)
    }

    suspend fun setMemberInfo(authKey: String, memberId: String, body: RequestBody) = safeApiCall {
        webService.setMemberInfo(authKey, memberId, body)
    }

    suspend fun setMemberValidation(authKey: String, body: RequestBody) = safeApiCall {
        webService.setMemberValidation(authKey, body)
    }

    suspend fun getLeaveReason(authKey: String) = safeApiCall {
        webService.getLeaveType(authKey)
    }

    suspend fun withdrawal(authKey: String, memberId: String, body: RequestBody) = safeApiCall {
        webService.withdrawalId(authKey, memberId, body)
    }

    suspend fun withdrawalV2(authKey: String, memberId: String, body: RequestBody) = safeApiCall {
        webService.withdrawalIdV2(authKey, memberId, body)
    }

    suspend fun getLocationHistoryList(authKey: String, memberId: String, startIndex:Int) = safeApiCall {
        webService.getLocationHistoryList(authKey, memberId, startIndex)
    }

    suspend fun checkValidationCI(authKey: String, ci: String) = safeApiCall {
        webService.checkValidationCI(authKey, ci)
    }

    suspend fun getFamilyProfile(authKey: String, memberId: String, familyCode:String) = safeApiCall {
        webService.getFamilyProfile(authKey, memberId, familyCode)
    }

    suspend fun registerFamilyProfile(authKey: String, body: RequestBody) = safeApiCall {
        webService.registerFamilyProfile(authKey, body)
    }

    suspend fun getInquiryType(authKey: String) = safeApiCall {
        webService.getInquiryType(authKey)
    }

    suspend fun uploadPersonalInquirys(
        authKey: String,
        memberId: MultipartBody.Part,
        inquiryType: MultipartBody.Part,
        title: MultipartBody.Part,
        content: MultipartBody.Part,
        appVersion: MultipartBody.Part,
        osVersion: MultipartBody.Part,
        deviceInfo: MultipartBody.Part,
        mobileCarrierInfo: MultipartBody.Part,
        deviceId: MultipartBody.Part,
        file: List<MultipartBody.Part>
    ) = safeApiCall {
        webService.uploadPersonalInquirys(authKey, memberId, inquiryType, title, content, appVersion, osVersion, deviceInfo, mobileCarrierInfo, deviceId, file)
    }

    suspend fun userLogout(authKey: String) = safeApiCall {
        webService.logout(authKey)
    }
}