package ai.comake.petping.ui.etc.mission_pet

import ai.comake.petping.*
import ai.comake.petping.api.Resource
import ai.comake.petping.data.repository.PetRepository
import ai.comake.petping.data.vo.ErrorResponse
import ai.comake.petping.data.vo.InsurancePet
import ai.comake.petping.data.vo.PetInsuranceMissionPet
import ai.comake.petping.util.getErrorBodyConverter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.ab180.airbridge.Airbridge
import co.ab180.airbridge.event.model.SemanticAttributes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * android-petping-2
 * Class: MissionPetViewModel
 * Created by cliff on 2022/02/16.
 *
 * Description:
 */
@HiltViewModel
class MissionPetViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var petRepository: PetRepository

    private val _petName = MutableLiveData<String>().apply { value = "" }
    val petName:LiveData<String> = _petName

    private val _petImageUrl = MutableLiveData<String>().apply { value = "" }
    val petImageUrl:LiveData<String> = _petImageUrl

    private val _settingDate = MutableLiveData<String>().apply { value = "" }
    val settingDate:LiveData<String> = _settingDate

    private val _applicationDate = MutableLiveData<String>().apply { value = "" }
    val applicationDate:LiveData<String> = _applicationDate

    private val _isChangeable = MutableLiveData<Boolean>().apply { value = false }
    val isChangeable:LiveData<Boolean> = _isChangeable

    private val _hasPet = MutableLiveData<Boolean>().apply { value = false }
    val hasPet:LiveData<Boolean> = _hasPet

    private val _showPetSelectPopup = MutableLiveData<Event<List<InsurancePet>>>()
    val showPetSelectPopup:LiveData<Event<List<InsurancePet>>> = _showPetSelectPopup

    private val _showChangePetErrorPopup = MutableLiveData<Event<ErrorResponse>>()
    val showChangePetErrorPopup:LiveData<Event<ErrorResponse>> = _showChangePetErrorPopup

    private val _showErrorPopup = MutableLiveData<Event<ErrorResponse>>()
    val showErrorPopup:LiveData<Event<ErrorResponse>> = _showErrorPopup

    private val _moveToPetInsuranceScreen = MutableLiveData<Event<Unit>>()
    val moveToPetInsuranceScreen:LiveData<Event<Unit>> = _moveToPetInsuranceScreen

    private val _insuranceMissionPetItems = MutableLiveData<List<PetInsuranceMissionPet>>()
    val insuranceMissionPetItems:LiveData<List<PetInsuranceMissionPet>> = _insuranceMissionPetItems

    private val _uiState = MutableLiveData<Event<UiState>>()
    val uiState: LiveData<Event<UiState>> = _uiState

    var petId = -1

    fun loadData() {
        getRepresentativePet()
        getInsuranceMissionPet()
    }

    fun goToPetInsurance() {
        _moveToPetInsuranceScreen.emit()
    }

    fun goToChangeRepresentationPet() = viewModelScope.launch {
        _uiState.emit(UiState.Loading)
        val response = petRepository.getCandidateRepreMisPets(
            AppConstants.AUTH_KEY,
            AppConstants.ID
        )

        when (response) {
            is Resource.Success -> {
                _uiState.emit(UiState.Success)
                _showPetSelectPopup.emit(response.value.data)
            }
            is Resource.Error -> {
                _uiState.emit(UiState.Failure(null))
                response.errorBody?.let { errorBody ->
                    _showChangePetErrorPopup.emit(errorBody)
                }
            }
        }
    }

    fun setRepresentativeMissionPets(petId:Int) = viewModelScope.launch {
        _uiState.emit(UiState.Loading)
        val body = makeMissionPetBody(petId)
        val response = petRepository.setRepresentativeMissionPet(
            AppConstants.AUTH_KEY,
            body
        )

        when (response) {
            is Resource.Success -> {
                _uiState.emit(UiState.Success)
                val eventValue = 10f
                val eventAttributes = mutableMapOf<String, String>()
                val semanticAttributes = SemanticAttributes()
                Airbridge.trackEvent(
                    "missionpet_event",
                    "missionpet_action",
                    "missionpet_label",
                    eventValue,
                    eventAttributes,
                    semanticAttributes.toMap()
                )

                getRepresentativePet()
            }
            is Resource.Error -> {
                _uiState.emit(UiState.Failure(null))
                response.errorBody?.let { errorBody ->
                    _showErrorPopup.emit(errorBody)
                }
            }
        }
    }

    fun getInsuranceMissionPet() = viewModelScope.launch {
        _uiState.emit(UiState.Loading)
        val response = petRepository.getPetInsuranceMissionPet(
            AppConstants.AUTH_KEY,
            AppConstants.ID
        )

        when (response) {
            is Resource.Success -> {
                _uiState.emit(UiState.Success)
                _insuranceMissionPetItems.value = response.value.data
            }
            else -> _uiState.emit(UiState.Failure(null))
        }
    }

    private fun getRepresentativePet() = viewModelScope.launch {
        _uiState.emit(UiState.Loading)
        val response = petRepository.getRepresentativePet(
            AppConstants.AUTH_KEY,
            AppConstants.ID
        )

        when (response) {
            is Resource.Success -> {
                _uiState.emit(UiState.Success)
                val data = response.value.data
                if (data.isEmpty()) {
                    _hasPet.value = false
                } else {
                    _hasPet.value = true
                    _petName.value = data[0].petName
                    _petImageUrl.value = data[0].profileImageURL
                    _applicationDate.value = data[0].applicationDate
                    _settingDate.value = data[0].settingDate
                    _isChangeable.value = data[0].isChangeable

                    petId = data[0].petId.toInt()
                }
            }
            else -> _uiState.emit(UiState.Failure(null))
        }
    }
}