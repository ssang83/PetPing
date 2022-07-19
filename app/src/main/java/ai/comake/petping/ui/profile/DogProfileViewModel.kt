package ai.comake.petping.ui.profile

import ai.comake.petping.AppConstants
import ai.comake.petping.Event
import ai.comake.petping.api.Resource
import ai.comake.petping.data.repository.PetRepository
import ai.comake.petping.emit
import ai.comake.petping.util.toAge
import ai.comake.petping.util.toGender
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * android-petping-2
 * Class: DogProfileViewModel
 * Created by cliff on 2022/02/24.
 *
 * Description:
 */
@HiltViewModel
class DogProfileViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var petRepository: PetRepository

    private val _profileUrl = MutableLiveData<String>()
    val profileUrl:LiveData<String> get() = _profileUrl

    private val _petName = MutableLiveData<String>()
    val petName:LiveData<String> get() = _petName

    private val _petInfo = MutableLiveData<String>()
    val petInfo:LiveData<String> get() = _petInfo

    private val _popupUrl = MutableLiveData<String>()
    val popupUrl:LiveData<String> get() = _popupUrl

    private val _viewMode = MutableLiveData<String>().apply { value = "" }
    val viewMode:LiveData<String> get() = _viewMode

    private val _needPopup = MutableLiveData<Boolean>().apply { value = false }
    val needPopup:LiveData<Boolean> get() = _needPopup

    val moveToSetting = MutableLiveData<Event<Int>>()
    val updateStatusBar = MutableLiveData<Event<String>>()
    val tabSelected = MutableLiveData<Event<Int>>()

    var petId = -1

    fun loadPetInfo(_petId:Int, viewMode:String) = viewModelScope.launch {
        val response = petRepository.getPetHistoryProfile(
            AppConstants.AUTH_KEY,
            _petId,
            AppConstants.ID,
            viewMode
        )
        when (response) {
            is Resource.Success -> {
                petId = _petId
                _petName.value = response.value.data.name
                _viewMode.value = viewMode
                _petInfo.value = "${response.value.data.breed} . ${response.value.data.birth.let { 
                    val tempBirth = response.value.data.birth.split("-")
                    tempBirth[0].substring(2,4) + tempBirth[1] + tempBirth[2]
                }.toAge()} . ${response.value.data.gender.toGender()}"

                _profileUrl.value = response.value.data.profileImageURL
            }
        }
    }

    fun goToSetting() {
        moveToSetting.emit(petId)
    }

    fun showPopupImage(imageUrl: String) {
        _popupUrl.value = imageUrl
        _needPopup.value = true
        updateStatusBar.emit("black")
    }

    fun closePopupImage() {
        _needPopup.value = false
        updateStatusBar.emit("white")
    }
}