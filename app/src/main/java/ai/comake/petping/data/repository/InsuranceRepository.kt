package ai.comake.petping.data.repository

import ai.comake.petping.api.WebService
import ai.comake.petping.data.repository.base.BaseRepository
import okhttp3.RequestBody
import javax.inject.Inject

/**
 * android-petping-2
 * Class: InsuranceRepository
 * Created by cliff on 2022/03/22.
 *
 * Description:
 */
class InsuranceRepository @Inject constructor(private val webService: WebService) : BaseRepository() {

    suspend fun getPetInsurJoins(authkey:String, memberId:String) = safeApiCall {
        webService.getPetInsurJoins(authkey, memberId)
    }

    suspend fun getCandidateInsurConnectPets(authkey: String, memberId: String) = safeApiCall {
        webService.getCandidateInsurConnectPets(authkey, memberId)
    }

    suspend fun getPetInsuranceCheck(authkey: String, memberId: String) = safeApiCall {
        webService.getPetInsuranceCheck(authkey, memberId)
    }

    suspend fun connectPetInsurance(authkey: String, body: RequestBody) = safeApiCall {
        webService.connectPetInsurance(authkey, body)
    }
}