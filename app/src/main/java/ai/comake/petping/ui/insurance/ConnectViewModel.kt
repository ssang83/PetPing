package ai.comake.petping.ui.insurance

import ai.comake.petping.*
import ai.comake.petping.api.Resource
import ai.comake.petping.data.repository.InsuranceRepository
import ai.comake.petping.data.repository.UserDataRepository
import ai.comake.petping.data.vo.ErrorResponse
import ai.comake.petping.util.HANGUEL_PATTERN
import ai.comake.petping.util.ID_FULL_PATTERN
import ai.comake.petping.util.getErrorBodyConverter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject

/**
 * android-petping-2
 * Class: ConnectViewModel
 * Created by cliff on 2022/03/22.
 *
 * Description:
 */
@HiltViewModel
class ConnectViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var repository: InsuranceRepository
    @Inject
    lateinit var userRepo:UserDataRepository

    val name = MutableLiveData<String>().apply { value = "" }
    val id = MutableLiveData<String>().apply { value = "" }
    var phoneNumber = MutableLiveData<String>().apply { value = "" }
    var isPhoneAuth = MutableLiveData<Boolean>().apply { value = false }
    val rewardAgree = MutableLiveData<Boolean>().apply { value = false }
    val birthAndGender = MutableLiveData<String>().apply { value = "" }

    val moveToAgreeScreen = MutableLiveData<Event<String>>()
    val authCompleteScreen = MutableLiveData<Event<Unit>>()
    val authCompleteErrorPopup = MutableLiveData<Event<ErrorResponse>>()
    val checkCIErrorPopup = MutableLiveData<Event<Unit>>()
    val checkCISuccess = MutableLiveData<Event<Unit>>()
    val uiState = MutableLiveData<Event<UiState>>()
    val moveToCertWeb = MutableLiveData<Event<Unit>>()

    var ci = ""
    var petId = -1

    fun getMemberInfo(_petId: Int) = viewModelScope.launch {
        uiState.emit(UiState.Loading)
        val response = userRepo.getMemberInfo(AppConstants.AUTH_KEY, AppConstants.ID)
        when (response) {
            is Resource.Success -> {
                uiState.emit(UiState.Success)
                petId = _petId
                response.value.data.name?.let { name.value = it }
                isPhoneAuth.value = response.value.data.isIdentityAuth
                response.value.data.birthAndGender?.let {
                    birthAndGender.value = it.substring(0, 6)
                }
            }
            else -> uiState.emit(UiState.Failure(null))
        }
    }

    fun checkValidationCI() = viewModelScope.launch {
        uiState.emit(UiState.Loading)
        val response = userRepo.checkValidationCI(AppConstants.AUTH_KEY, ci)
        when (response) {
            is Resource.Success -> {
                uiState.emit(UiState.Success)
                checkCISuccess.emit()
            }
            else -> {
                uiState.emit(UiState.Failure(null))
                checkCIErrorPopup.emit()
            }
        }
    }

    fun isValid(name: String, id: String, phoneNumber: String, isPhoneAuth:Boolean, rewardAgree:Boolean): Boolean {
        if (isPhoneAuth.not()) {
            return Pattern.compile(HANGUEL_PATTERN).matcher(name).matches() && Pattern.compile(
                ID_FULL_PATTERN
            ).matcher(id).matches() && phoneNumber != "" && ci != ""
        } else {
            return Pattern.compile(HANGUEL_PATTERN).matcher(name).matches() && Pattern.compile(
                ID_FULL_PATTERN
            ).matcher(id).matches() && rewardAgree
        }
    }

    fun onAgreementChanged(status:Boolean) {
        rewardAgree.value = status
    }

    fun onMoveToAgree() {
        if(BuildConfig.FLAVOR == "dev"){
            moveToAgreeScreen.emit("https://dev.petping.com/petping/policy/agree-for-reward")
        } else {
            moveToAgreeScreen.emit("https://www.petping.com/petping/policy/agree-for-reward")
        }
    }

    /**
     * 연결 완료 버튼 클릭 시
     */
    fun goToConnect() = viewModelScope.launch {
        uiState.emit(UiState.Loading)
        val body = makeInsuranceConnectBody(
            petId,
            id.value.toString(),
            name.value.toString()
        )
        val response = repository.connectPetInsurance(AppConstants.AUTH_KEY, body)
        when (response) {
            is Resource.Success -> {
                uiState.emit(UiState.Success)
                authCompleteScreen.emit()
            }
            is Resource.Failure -> {
                uiState.emit(UiState.Failure(null))
                response.errorBody?.let { errorBody ->
                    val errorResponse = getErrorBodyConverter().convert(errorBody)!!
                    authCompleteErrorPopup.emit(errorResponse)
                }
            }
        }
    }

    /**
     * 휴대폰 본인인증
     */
    fun goToCertification() {
        moveToCertWeb.emit()
    }
}