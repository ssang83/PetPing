package ai.comake.petping.ui.login

import ai.comake.petping.AppConstants.SAPA_KEY
import ai.comake.petping.Event
import ai.comake.petping.UiState
import ai.comake.petping.api.Resource
import ai.comake.petping.data.repository.LoginRepository
import ai.comake.petping.data.vo.ErrorResponse
import ai.comake.petping.emit
import ai.comake.petping.util.EMAIL_PATTERN
import ai.comake.petping.util.PASSWORD_PATTERN
import ai.comake.petping.util.getErrorBodyConverter
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject

/**
 * android-petping-2
 * Class: SignUpEmailViewModel
 * Created by cliff on 2022/03/14.
 *
 * Description:
 */
@HiltViewModel
class SignUpEmailViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var loginRepository: LoginRepository

    val email = MutableLiveData<String>().apply { value = "" }
    val password = MutableLiveData<String>().apply { value = "" }
    val passwordConfirm = MutableLiveData<String>().apply { value = "" }
    val isDuplicate = MutableLiveData<Boolean>().apply { value = true }

    val emailErrorPopup = MutableLiveData<Event<ErrorResponse>>()
    val emailError = MutableLiveData<Event<ErrorResponse>>()
    val emailSuccess = MutableLiveData<Event<Unit>>()
    val uiState = MutableLiveData<Event<UiState>>()
    val passwordUiUpdate = MutableLiveData<Event<Unit>>()
    val passwordConfirmUiUpdate = MutableLiveData<Event<Unit>>()

    /**
     * 입력 중 이메일 패턴 체크, 이메일 중복 체크
     */
    val emailTextWatcher = object : TextWatcher {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable?) {
            if (Pattern.compile(EMAIL_PATTERN).matcher(s.toString()).matches()) {
                emailCheck(s.toString())
            }
        }
    }

    /**
     * 비밀번호 띄어쓰기 불가
     */
    val passwordTextWatcher = object : TextWatcher {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            password.value = password.value!!.replace(" ", "")
            passwordUiUpdate.emit()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable?) {}
    }

    /**
     * 비밀번호 확인 띄어쓰기 불가
     */
    val passwordConfirmTextWatcher = object : TextWatcher {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            passwordConfirm.value = passwordConfirm.value!!.replace(" ", "")
            passwordConfirmUiUpdate.emit()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable?) {}
    }

    /**
     * 이메일 패턴, 비밀번호 패턴 체크, 비밀번호 확인 체크, 이메일 중복 체크 모두 true 이면 다음 단계로 버튼 활성화
     */
    fun isValidInfo(email: String, password: String, passwordConfirm: String): Boolean {
        return Pattern.compile(EMAIL_PATTERN).matcher(email).matches() &&
                Pattern.compile(PASSWORD_PATTERN).matcher(password).matches() &&
                passwordConfirm == password && !(isDuplicate.value ?:false)
    }

    fun goToNext() = viewModelScope.launch {
        uiState.emit(UiState.Loading)
        val response = loginRepository.isDuplicationEmail(SAPA_KEY, email.value.toString())
        when (response) {
            is Resource.Success -> {
                uiState.emit(UiState.Success)
                emailSuccess.emit()
            }
            is Resource.Failure -> {
                uiState.emit(UiState.Failure(null))
                response.errorBody?.let { errorBody ->
                    val errorResponse = getErrorBodyConverter().convert(errorBody)!!
                    emailErrorPopup.emit(errorResponse)
                }
            }
        }
    }

    fun emailCheck(email: String) = viewModelScope.launch {
        val response = loginRepository.isDuplicationEmail(SAPA_KEY, email)
        when (response) {
            is Resource.Success -> isDuplicate.value = false
            is Resource.Failure -> {
                isDuplicate.value = true
                response.errorBody?.let { errorBody ->
                    val errorResponse = getErrorBodyConverter().convert(errorBody)!!
                    emailError.emit(errorResponse)
                }
            }
        }
    }
}