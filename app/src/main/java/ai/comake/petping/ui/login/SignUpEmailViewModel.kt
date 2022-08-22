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
import android.view.View
import android.widget.EditText
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

    // 인터렉션 관련 사용하는 변수들...
    val emailInputStatus = MutableLiveData<Boolean>().apply { value = false }
    val emailValidation = MutableLiveData<Boolean>().apply { value = true }
    val emailClear = MutableLiveData<Boolean>().apply { value = false }
    val emailFocusHintVisible = MutableLiveData<Boolean>().apply { value = false }
    val emailLineStatus = MutableLiveData<Boolean>().apply { value = false }
    val emailInitialErrorText = MutableLiveData<Boolean>().apply { value = true }
    val passwdValidation = MutableLiveData<Boolean>().apply { value = true }
    val passwdHelperVisible = MutableLiveData<Boolean>().apply { value = false }
    val passwdInputStatus = MutableLiveData<Boolean>().apply { value = false }
    val passwdClear = MutableLiveData<Boolean>().apply { value = false }
    val passwdFocusHintVisible = MutableLiveData<Boolean>().apply { value = false }
    val passwdLineStatus = MutableLiveData<Boolean>().apply { value = false }
    val passwdConfirmValidation = MutableLiveData<Boolean>().apply { value = true }
    val passwdConfirmInputStatus = MutableLiveData<Boolean>().apply { value = false }
    val passwdConfirmClear = MutableLiveData<Boolean>().apply { value = false }
    val passwdConfirmFocusHintVisible = MutableLiveData<Boolean>().apply { value = false }
    val passwdConfirmLineStatus = MutableLiveData<Boolean>().apply { value = false }

    val emailErrorPopup = MutableLiveData<Event<ErrorResponse>>()
    val emailSuccess = MutableLiveData<Event<Unit>>()
    val uiState = MutableLiveData<Event<UiState>>()
    val passwordUiUpdate = MutableLiveData<Event<Unit>>()
    val passwordConfirmUiUpdate = MutableLiveData<Event<Unit>>()

    val emailFocusChangeListener = object : View.OnFocusChangeListener {
        override fun onFocusChange(v: View?, hasFocus: Boolean) {
            val str = (v as EditText).text.toString()
            if (hasFocus) {
                emailFocusHintVisible.value = true
                emailLineStatus.value = true

                emailInputStatus.apply {
                    if (str.isNotEmpty()) {
                        value = true
                    }
                }
            } else {
                emailFocusHintVisible.apply {
                    if (str.isNotEmpty()) {
                        value = true
                    } else {
                        value = false
                    }
                }

                emailLineStatus.value = false
                emailInputStatus.value = false
            }
        }
    }

    val passwdFocusChangedListener = object : View.OnFocusChangeListener {
        override fun onFocusChange(v: View?, hasFocus: Boolean) {
            val str = (v as EditText).text.toString()
            if (hasFocus) {
                passwdFocusHintVisible.value = true
                passwdLineStatus.value = true
                passwdHelperVisible.value = true

                passwdHelperVisible.apply {
                    if (str.isNotEmpty()) {
                        value = false
                    } else {
                        value = true
                    }
                }

                passwdInputStatus.apply {
                    if (str.isNotEmpty()) {
                        value = true
                    } else {
                        value = false
                    }
                }
            } else {
                passwdFocusHintVisible.apply {
                    if (str.isNotEmpty()) {
                        value = true
                    } else {
                        value = false
                    }
                }

                passwdLineStatus.value = false
                passwdInputStatus.value = false
                passwdHelperVisible.value = false
            }
        }
    }

    val passwdConfirmFocusChangedListener = object : View.OnFocusChangeListener {
        override fun onFocusChange(v: View?, hasFocus: Boolean) {
            val str = (v as EditText).text.toString()
            if (hasFocus) {
                passwdConfirmFocusHintVisible.value = true
                passwdConfirmLineStatus.value = true

                passwdConfirmInputStatus.apply {
                    if (str.isNotEmpty()) {
                        value = true
                    } else {
                        value = false
                    }
                }
            } else {
                passwdConfirmFocusHintVisible.apply {
                    if (str.isNotEmpty()) {
                        value = true
                    } else {
                        value = false
                    }
                }

                passwdConfirmLineStatus.value = false
                passwdConfirmInputStatus.value = false
            }
        }
    }

    /**
     * 입력 중 이메일 패턴 체크, 이메일 중복 체크
     */
    val emailTextWatcher = object : TextWatcher {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            emailInitialErrorText.value = true

            emailInputStatus.apply {
                if (s?.length!! > 0) {
                    value = true
                } else {
                    value = false
                }
            }

            emailValidation.apply {
                if (s?.length!! > 0) {
                    if (Pattern.compile(EMAIL_PATTERN).matcher(s.toString()).matches()) {
                        value = true
                    } else {
                        value = false
                    }
                } else {
                    value = true
                }
            }
        }
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
            passwdInputStatus.apply {
                if (s?.length!! > 0) {
                    value = true
                } else {
                    value = false
                }
            }

            passwdValidation.apply {
                if (s?.length!! > 0) {
                    if (Pattern.compile(PASSWORD_PATTERN).matcher(s.toString()).matches()) {
                        value = true
                    } else {
                        value = false
                        passwdHelperVisible.value = false
                    }
                } else {
                    value = true
                }
            }

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
            passwdConfirmInputStatus.apply {
                if (s?.length!! > 0) {
                    value = true
                } else {
                    value = false
                }
            }

            passwdConfirmValidation.apply {
                if (s?.length!! > 0) {
                    if (password.value.toString() == s.toString()) {
                        value = true
                    } else {
                        value = false
                    }
                } else {
                    value = true
                }
            }

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
            is Resource.Error -> {
                uiState.emit(UiState.Failure(null))
                emailInitialErrorText.value = false
                emailValidation.value = false
                response.errorBody?.let { errorBody ->
                    emailErrorPopup.emit(errorBody)
                }
            }
        }
    }

    fun emailCheck(email: String) = viewModelScope.launch {
        val response = loginRepository.isDuplicationEmail(SAPA_KEY, email)
        when (response) {
            is Resource.Success -> isDuplicate.value = false
            is Resource.Error -> {
                isDuplicate.value = true
                response.errorBody?.let { errorBody ->
                    if (errorBody.code == "C3000") {
                        emailInitialErrorText.value = false
                        emailValidation.value = false
                    }
                }
            }
        }
    }

    fun onInputEmailClear() {
        emailClear.value = true
    }

    fun onInputPasswdClear() {
        passwdClear.value = true
    }

    fun onInputPasswdConfirmClear() {
        passwdConfirmClear.value = true
    }
}