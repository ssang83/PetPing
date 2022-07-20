package ai.comake.petping.ui.etc.member_info

import ai.comake.petping.*
import ai.comake.petping.api.Resource
import ai.comake.petping.data.repository.UserDataRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * android-petping-2
 * Class: MemerInfoViewModel
 * Created by cliff on 2022/02/22.
 *
 * Description:
 */
@HiltViewModel
class MemberInfoViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var userDataRepository: UserDataRepository

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> get() = _email

    private val _marketing = MutableLiveData<String>().apply { value = "동의함" }
    val marketing: LiveData<String> get() = _marketing

    private val _login = MutableLiveData<String>().apply { value = "이메일 로그인" }
    val login: LiveData<String> get() = _login

    private val _phone = MutableLiveData<String>()
    val phone: LiveData<String> get() = _phone

    private val _name = MutableLiveData<String>()
    val name: LiveData<String> get() = _name

    private val _birth = MutableLiveData<String>()
    val birth: LiveData<String> get() = _birth

    private val _gender = MutableLiveData<String>()
    val gender: LiveData<String> get() = _gender

    private val _birthAndGender = MutableLiveData<String>()
    val birthAndGender: LiveData<String> get() = _birthAndGender

    private val _isEmailAuth = MutableLiveData<Boolean>().apply { value = true }
    val isEmailAuth: LiveData<Boolean> get() = _isEmailAuth

    private val _isVisibleEmailSend = MutableLiveData<Boolean>().apply { value = false }
    val isVisibleEmailSend: LiveData<Boolean> get() = _isVisibleEmailSend

    val emailAuthSuccessPopup = MutableLiveData<Event<Unit>>()
    val moveToWithDrawal = MutableLiveData<Event<Unit>>()
    val moveToChangePw = MutableLiveData<Event<Unit>>()
    val moveToChangePhoneNumber = MutableLiveData<Event<Unit>>()
    val moveToAddPhoneNumber = MutableLiveData<Event<Unit>>()
    val moveToChangeEmail = MutableLiveData<Event<Unit>>()
    val moveToRegisterEmail = MutableLiveData<Event<Unit>>()
    val moveToLocationHistory = MutableLiveData<Event<Unit>>()
    val logout = MutableLiveData<Event<Unit>>()
    val uiState = MutableLiveData<Event<UiState>>()

    fun loadData() = viewModelScope.launch {
        uiState.emit(UiState.Loading)
        val response = userDataRepository.getMemberInfo(AppConstants.AUTH_KEY, AppConstants.ID)
        when (response) {
            is Resource.Success -> {
                uiState.emit(UiState.Success)
                val data = response.value.data
                data.email?.let {
                    _email.value = it
                    _isVisibleEmailSend.value = it.isNotEmpty() && data.isEmailAuth.not()
                }

                data.name?.let { _name.value = it }
                data.birthAndGender?.let { _birthAndGender.value = it }

                _marketing.value = if (data.pushMarketingInfo) {
                    "동의함"
                } else {
                    "동의안함"
                }

                _login.value = data.typeStr
                _phone.value = data.phone

                data.birthAndGender?.let {
                    _birth.value = data.birthAndGender.substring(0, 6)
                    if ("1" == data.birthAndGender.substring(data.birthAndGender.length - 1)
                        || "3" == data.birthAndGender.substring(data.birthAndGender.length - 1)
                    ) {
                        _gender.value = "남성"
                    } else {
                        _gender.value = "여성"
                    }
                }

                _isEmailAuth.value = data.isEmailAuth
            }
            is Resource.Failure -> uiState.emit(UiState.Failure(null))
        }
    }

    fun goToWithdrawal() {
        moveToWithDrawal.emit()
    }

    fun goToChangePassword() {
        moveToChangePw.emit()
    }

    fun sendAuthEmail() {
        sendEmailAuth()
    }

    fun goToChangePhoneNumber() {
        moveToChangePhoneNumber.emit()
    }

    fun goToAddPhoneNumber() {
        moveToAddPhoneNumber.emit()
    }

    fun goToChangeEmail() {
        moveToChangeEmail.emit()
    }

    fun goToRegisterEmail() {
        moveToRegisterEmail.emit()
    }

    fun goToLocationHistory() {
        moveToLocationHistory.emit()
    }

    fun logout() {
        logout.emit()
    }

    private fun sendEmailAuth() = viewModelScope.launch {
        uiState.emit(UiState.Loading)
        val body = makeAuthMailBody(email.toString())
        val response = userDataRepository.sendEmailAuth(AppConstants.AUTH_KEY, body)
        when (response) {
            is Resource.Success -> {
                uiState.emit(UiState.Success)
                emailAuthSuccessPopup.emit()
            }
            is Resource.Failure -> {
                uiState.emit(UiState.Failure(null))
            }
        }
    }
}