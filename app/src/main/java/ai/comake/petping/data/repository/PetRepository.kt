package ai.comake.petping.data.repository

import ai.comake.petping.api.WebService
import ai.comake.petping.data.repository.base.BaseRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

/**
 * android-petping-2
 * Class: PetRepository
 * Created by cliff on 2022/02/11.
 *
 * Description:
 */
class PetRepository @Inject constructor(private val webService: WebService) : BaseRepository() {

    suspend fun getPetList(authkey:String, memberId:String) = safeApiCall {
        webService.getPetList(authkey, memberId)
    }

    suspend fun getRepresentativePet(authkey:String, memberId:String) = safeApiCall {
        webService.getRepresentativePet(authkey, memberId)
    }

    suspend fun getPetInsuranceMissionPet(authkey:String, memberId:String) = safeApiCall {
        webService.getPetInsuranceMissionPet(authkey, memberId)
    }

    suspend fun getCandidateRepreMisPets(authkey:String, memberId:String) = safeApiCall {
        webService.getCandidateRepreMisPets(authkey, memberId)
    }

    suspend fun setRepresentativeMissionPet(authkey:String, body: RequestBody) = safeApiCall {
        webService.setRepresentativeMissionPet(authkey, body)
    }

    suspend fun getPetHistoryProfile(authkey: String, petId:Int, memberId: String, viewMode:String) = safeApiCall {
        webService.getWalkHistoryProfile(authkey, petId, memberId, viewMode)
    }

    suspend fun modifyPetRegisterNumber(authkey: String, petId: Int, body: RequestBody) = safeApiCall {
        webService.modifyPetRegisterNumber(authkey, petId, body)
    }

    suspend fun getPetProfile(authkey: String, petId: Int, memberId: String) = safeApiCall {
        webService.getPetProfile(authkey, petId, memberId)
    }

    suspend fun unlinkFamilyCode(authkey: String, profileId:Int, body: RequestBody) = safeApiCall {
        webService.unlinkFamily(authkey, profileId, body)
    }

    suspend fun modifyPetCharacter(authkey: String, petCharId:Int, body: RequestBody) = safeApiCall {
        webService.modifyPetCharacter(authkey, petCharId, body)
    }

    suspend fun modifyPetProfile(authkey: String, petId: Int, body: Map<String, @JvmSuppressWildcards RequestBody>) = safeApiCall {
        webService.modifyPetProfile(authkey, petId, body)
    }

    suspend fun modifyPetProfileWithFile(
        authkey: String,
        petId: Int,
        body: Map<String, @JvmSuppressWildcards RequestBody>,
        file: MultipartBody.Part
    ) = safeApiCall {
        webService.modifyPetProfileWithFile(authkey, petId, body, file)
    }

    suspend fun getBreedList(authkey: String) = safeApiCall {
        webService.getBreedList(authkey)
    }

    suspend fun deletePetProfile(authkey: String, profileId:Int) = safeApiCall {
        webService.deletePetProfile(authkey, profileId)
    }

    suspend fun makeProfile(
        authkey: String,
        body: Map<String, @JvmSuppressWildcards RequestBody>,
        file: MultipartBody.Part?
    ) = safeApiCall {
        webService.requestMakeProfile(authkey, body, file)
    }

//    suspend fun makeProfile(authkey: String, body: Map<String, @JvmSuppressWildcards RequestBody>) = safeApiCall {
//        webService.requestMakeProfile(authkey, body)
//    }
//
//    suspend fun makeProfileWithFile(
//        authkey: String,
//        body: Map<String, @JvmSuppressWildcards RequestBody>,
//        file: MultipartBody.Part
//    ) = safeApiCall {
//        webService.requestMakeProfileWithFile(authkey, body, file)
//    }
}