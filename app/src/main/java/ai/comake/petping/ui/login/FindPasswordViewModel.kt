package ai.comake.petping.ui.login

import ai.comake.petping.*
import ai.comake.petping.AppConstants.SAPA_KEY
import ai.comake.petping.api.Resource
import ai.comake.petping.data.repository.LoginRepository
import ai.comake.petping.data.vo.ErrorResponse
import ai.comake.petping.util.EMAIL_PATTERN
import ai.comake.petping.util.getErrorBodyConverter
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
 * Class: FindPasswordViewModel
 * Created by cliff on 2022/03/11.
 *
 * Description:
 */
@HiltViewModel
class FindPasswordViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var loginRepository: LoginRepository

    // 인터렉션 관련 사용하는 변수들...
    val emailInputStatus = MutableLiveData<Boolean>().apply { value = false }
    val emailValidation = MutableLiveData<Boolean>().apply { value = true }
    val emailClear = MutableLiveData<Boolean>().apply { value = false }
    val emailFocusHintVisible = MutableLiveData<Boolean>().apply { value = false }
    val emailLineStatus = MutableLiveData<Boolean>().apply { value = false }

    val email = MutableLiveData<String>().apply { value = "" }
    val emailErrorPopup = MutableLiveData<Event<ErrorResponse>>()
    val emailSuccessPopup = MutableLiveData<Event<Unit>>()
    val moveToLogin = MutableLiveData<Event<Unit>>()
    val uiState = MutableLiveData<Event<UiState>>()

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

    fun sendEmail() = viewModelScope.launch {
        uiState.emit(UiState.Loading)
        val body = makeFindPasswordBody(email.value.toString())
        val response = loginRepository.findPassword(SAPA_KEY, body)
        when (response) {
            is Resource.Success -> {
                uiState.emit(UiState.Success)
                emailSuccessPopup.emit()
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

    fun isValidInfo(email: String): Boolean {
        return Pattern.compile(EMAIL_PATTERN).matcher(email).matches()
    }

    fun onEmailTextChanged(text: CharSequence) {

        emailInputStatus.apply {
            if (text.length > 0) {
                value = true
            } else {
                value = false
            }
        }

        emailValidation.apply {
            if (text.length > 0) {
                if (Pattern.compile(EMAIL_PATTERN).matcher(text.toString()).matches()) {
                    value = true
                } else {
                    value = false
                }
            } else {
                value = true
            }
        }
    }

    fun onInputEmailClear() {
        emailClear.value = true
    }
}