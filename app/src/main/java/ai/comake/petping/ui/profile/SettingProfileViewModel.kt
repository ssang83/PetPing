package ai.comake.petping.ui.profile

import ai.comake.petping.AppConstants
import ai.comake.petping.Event
import ai.comake.petping.UiState
import ai.comake.petping.api.Resource
import ai.comake.petping.data.repository.PetRepository
import ai.comake.petping.data.vo.PetProfileData
import ai.comake.petping.emit
import ai.comake.petping.util.toGender
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * android-petping-2
 * Class: SettingProfileViewModel
 * Created by cliff on 2022/03/23.
 *
 * Description:
 */
@HiltViewModel
class SettingProfileViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var repo: PetRepository

    private val _profileImage = MutableLiveData<String>().apply { value = "" }
    val profileImage:LiveData<String> get() = _profileImage

    private val _name = MutableLiveData<String>().apply { value = "" }
    val name:LiveData<String> get() = _name

    private val _gender = MutableLiveData<String>().apply { value = "" }
    val gender:LiveData<String> get() = _gender

    private val _breed = MutableLiveData<String>().apply { value = "" }
    val breed:LiveData<String> get() = _breed

    private val _birth = MutableLiveData<String>().apply { value = "" }
    val birth:LiveData<String> get() = _birth

    private val _weight = MutableLiveData<String>().apply { value = "" }
    val weight:LiveData<String> get() = _weight

    private val _regNo = MutableLiveData<String>().apply { value = "" }
    val regNo:LiveData<String> get() = _regNo

    private val _isWalk = MutableLiveData<Boolean>().apply { value = false }
    val isWalk:LiveData<Boolean> get() = _isWalk

    private val _isMissionPet = MutableLiveData<Boolean>().apply { value = false }
    val isMissionPet:LiveData<Boolean> get() = _isMissionPet

    private val _isPublic = MutableLiveData<Boolean>().apply { value = true }
    val isPublic:LiveData<Boolean> get() = _isPublic

    val moveToEdit = MutableLiveData<Event<Unit>>()
    val moveToRegisterRNS = MutableLiveData<Event<Unit>>()
    val moveToMissionPet = MutableLiveData<Event<Unit>>()
    val uiState = MutableLiveData<Event<UiState>>()

    fun loadData(petId:Int) = viewModelScope.launch {
        uiState.emit(UiState.Loading)
        val response = repo.getPetProfile(AppConstants.AUTH_KEY, petId, AppConstants.ID)
        when (response) {
            is Resource.Success -> {
                uiState.emit(UiState.Success)
                _profileImage.value = response.value.data.profileImageURL
                _name.value = response.value.data.name
                _gender.value = response.value.data.gender.toGender()
                _breed.value = response.value.data.breed
                _birth.value = getBirthDate(response.value.data.birth)
                _weight.value = "${response.value.data.weight} kg"
                _regNo.value = response.value.data.petRn
                _isWalk.value = response.value.data.isPossibleWalk
                _isMissionPet.value = response.value.data.isMissionPetSetting
                _isPublic.value = response.value.data.isPublic
            }
            else -> uiState.emit(UiState.Failure(null))
        }
    }

    fun goToEdit() {
        moveToEdit.emit()
    }

    fun goToRegisterRNS() {
        moveToRegisterRNS.emit()
    }

    fun goToMissionPet() {
        moveToMissionPet.emit()
    }

    private fun getBirthDate(birth: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
        val newSdf = SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREA)
        return newSdf.format(sdf.parse(birth))
    }
}