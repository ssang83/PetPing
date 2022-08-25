package ai.comake.petping.ui.insurance

import ai.comake.petping.AppConstants
import ai.comake.petping.Event
import ai.comake.petping.UiState
import ai.comake.petping.api.Resource
import ai.comake.petping.data.repository.InsuranceRepository
import ai.comake.petping.data.repository.PetRepository
import ai.comake.petping.data.vo.ErrorResponse
import ai.comake.petping.data.vo.InsurancePet
import ai.comake.petping.data.vo.Pet
import ai.comake.petping.data.vo.PetInsurJoinsData
import ai.comake.petping.emit
import ai.comake.petping.util.Coroutines
import ai.comake.petping.util.getErrorBodyConverter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * android-petping-2
 * Class: InsuranceHistoryViewModel
 * Created by cliff on 2022/03/22.
 *
 * Description:
 */
@HiltViewModel
class InsuranceHistoryViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var repo: InsuranceRepository

    @Inject
    lateinit var petRepo: PetRepository

    private val _insuranceDataList = MutableLiveData<List<PetInsurJoinsData>>()
    val insuranceDataList:LiveData<List<PetInsurJoinsData>> get() = _insuranceDataList

    val petAuthPopup = MutableLiveData<Event<Unit>>()
    val petAuthErrorPopup = MutableLiveData<Event<ErrorResponse>>()
    val petJoinErrorPopup = MutableLiveData<Event<ErrorResponse>>()
    val moveToAuthScreen = MutableLiveData<Event<Int>>()
    val moveToInsuranceScreen = MutableLiveData<Event<String>>()
    val petEmptyPopup = MutableLiveData<Event<Unit>>()
    val petSelectPopup = MutableLiveData<Event<List<InsurancePet>>>()
    val uiState = MutableLiveData<Event<UiState>>()
    val moveToInsurnaceDetail = MutableLiveData<Event<PetInsurJoinsData>>()

    private val _showProfilePopup = MutableLiveData<Event<Unit>>()
    val showProfilePopup:LiveData<Event<Unit>> get() = _showProfilePopup

    /**
     * 펫보험 데이터 받아오기
     */
    fun loadData() = viewModelScope.launch {
        uiState.emit(UiState.Loading)
        val response = repo.getPetInsurJoins(AppConstants.AUTH_KEY, AppConstants.ID)
        when (response) {
            is Resource.Success -> {
                uiState.emit(UiState.Success)
                _insuranceDataList.value = response.value.data
            }
            else -> uiState.emit(UiState.Failure(null))
        }
    }

    fun petInsuranceAuth() = viewModelScope.launch {
        uiState.emit(UiState.Loading)
        val response = repo.getCandidateInsurConnectPets(AppConstants.AUTH_KEY, AppConstants.ID)
        when (response) {
            is Resource.Success -> {
                uiState.emit(UiState.Success)
                if (response.value.data.size == 1) {
                    moveToAuthScreen.emit(response.value.data[0].petId)
                } else if(response.value.data.isEmpty()) {
                    petEmptyPopup.emit()
                } else {
                    petSelectPopup.emit(response.value.data)
                }
            }
            is Resource.Failure -> {
                uiState.emit(UiState.Failure(null))
                response.errorBody?.let { errorBody ->
                    val errorResponse = getErrorBodyConverter().convert(errorBody)!!
                    petAuthErrorPopup.emit(errorResponse)
                }
            }
        }
    }

    /**
     * 펫보험 인증하기 버튼 클릭 시
     */
    fun findCandidate() {
        petAuthPopup.emit()
    }

    /**
     * 펫보험 상세로 이동한다.
     *
     */
    fun goToInsuranceDetail(item: PetInsurJoinsData) {
        moveToInsurnaceDetail.emit(item)
    }

    fun goToConnectPage(petId: Int) {
        moveToAuthScreen.emit(petId)
    }

    fun getPetList() = Coroutines.main(this) {
        val response = petRepo.getPetList(AppConstants.AUTH_KEY, AppConstants.ID)
        when (response) {
            is Resource.Success -> {
                if (response.value.data.pets.size > 0 && response.value.data.pets[0].isFamilyProfile.not()) {
                    petJoinInsurance()
                } else {
                    _showProfilePopup.emit()
                }
            }
            is Resource.Error -> {
                response.errorBody?.let { error ->
                    if (error.code == "C2070") {
                        _showProfilePopup.emit()
                    }
                }
            }
        }
    }

    private fun petJoinInsurance() = viewModelScope.launch {
        uiState.emit(UiState.Loading)
        val response = repo.getPetInsuranceCheck(AppConstants.AUTH_KEY, AppConstants.ID)
        when (response) {
            is Resource.Success -> {
                uiState.emit(UiState.Success)
                moveToInsuranceScreen.emit(response.value.data.insurJoinURL)
            }
            is Resource.Failure -> {
                uiState.emit(UiState.Failure(null))
                response.errorBody?.let { errorBody ->
                    val errorResponse = getErrorBodyConverter().convert(errorBody)!!
                    petJoinErrorPopup.emit(errorResponse)
                }
            }
        }
    }
}