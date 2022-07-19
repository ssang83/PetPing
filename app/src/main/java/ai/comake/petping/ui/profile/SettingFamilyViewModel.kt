package ai.comake.petping.ui.profile

import ai.comake.petping.*
import ai.comake.petping.api.Resource
import ai.comake.petping.data.repository.PetRepository
import ai.comake.petping.data.vo.MemberInfoFamilyReg
import ai.comake.petping.data.vo.PetProfileData
import ai.comake.petping.util.LogUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * android-petping-2
 * Class: SettingFamilyViewModel
 * Created by cliff on 2022/03/23.
 *
 * Description:
 */
@HiltViewModel
class SettingFamilyViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var repo : PetRepository

    private val _familyCode = MutableLiveData<String>().apply { value = "" }
    val familyCode:LiveData<String> get() = _familyCode

    private val _memberInfos = MutableLiveData<List<MemberInfoFamilyReg>>()
    val memberInfos:LiveData<List<MemberInfoFamilyReg>> get() = _memberInfos

    val moveToFamilyInviteGuide = MutableLiveData<Event<Unit>>()
    val copyToClipboard = MutableLiveData<Event<String>>()
    val unlinkFamilyPopup = MutableLiveData<Event<Unit>>()
    val uiState = MutableLiveData<Event<UiState>>()

    var petProfileData: PetProfileData? = null
    var targetProfileId = -1

    fun loadData(petId:Int) = viewModelScope.launch {
        val response = repo.getPetProfile(AppConstants.AUTH_KEY, petId, AppConstants.ID)
        when (response) {
            is Resource.Success -> {
                petProfileData = response.value.data
                _familyCode.value = response.value.data.myFamilyCode
                _memberInfos.value = response.value.data.memberInfoFamilyRegs
            }
        }
    }

    fun methodFamilyInvited() {
        moveToFamilyInviteGuide.emit()
    }

    fun copyToClipboard(code:String) {
        copyToClipboard.emit(code)
    }

    fun unlinkFamily(profileId: Int) {
        targetProfileId = profileId
        unlinkFamilyPopup.emit()
    }

    fun unlinkFamilyCode() = viewModelScope.launch {
        uiState.emit(UiState.Loading)
        val body = makeUnlinkFamilyBody(
            petProfileData?.petId ?: -1,
            petProfileData?.profileId ?: -1,
            petProfileData?.myFamilyCode ?: ""
        )
        val response = repo.unlinkFamilyCode(AppConstants.AUTH_KEY, targetProfileId, body)
        when (response) {
            is Resource.Success -> {
                uiState.emit(UiState.Success)
                loadData(petProfileData?.petId ?: -1)
            }
            else -> uiState.emit(UiState.Failure(null))
        }
    }
}