package ai.comake.petping.ui.login

import ai.comake.petping.*
import ai.comake.petping.AppConstants.SAPA_KEY
import ai.comake.petping.api.Resource
import ai.comake.petping.data.repository.LoginRepository
import ai.comake.petping.data.vo.ErrorResponse
import ai.comake.petping.data.vo.UserDataStore
import ai.comake.petping.util.EMAIL_PATTERN
import ai.comake.petping.util.LogUtil
import ai.comake.petping.util.SharedPreferencesManager
import ai.comake.petping.util.getErrorBodyConverter
import android.content.Context
import android.text.TextUtils
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
 * Class: EmailLoginViewModel
 * Created by cliff on 2022/03/10.
 *
 * Description:
 */
@HiltViewModel
class EmailLoginViewModel @Inject constructor() : ViewModel() {
    @Inject
    lateinit var sharedPreferencesManager: SharedPreferencesManager

    @Inject
    lateinit var loginRepository: LoginRepository

    val email = MutableLiveData<String>().apply { value = "" }
    val password = MutableLiveData<String>().apply { value = "" }

    // 인터렉션 관련 사용하는 변수들...
    val emailInputStatus = MutableLiveData<Boolean>().apply { value = false }
    val emailValidation = MutableLiveData<Boolean>().apply { value = true }
    val emailClear = MutableLiveData<Boolean>().apply { value = false }
    val emailFocusHintVisible = MutableLiveData<Boolean>().apply { value = false }
    val emailLineStatus = MutableLiveData<Boolean>().apply { value = false }
    val passwdInputStatus = MutableLiveData<Boolean>().apply { value = false }
    val passwdClear = MutableLiveData<Boolean>().apply { value = false }
    val passwdFocusHintVisible = MutableLiveData<Boolean>().apply { value = false }
    val passwdLineStatus = MutableLiveData<Boolean>().apply { value = false }

    val emailErrorPopup = MutableLiveData<Event<ErrorResponse>>()
    val passwordErrorPopup = MutableLiveData<Event<ErrorResponse>>()
    val loginErrorPopup = MutableLiveData<Event<ErrorResponse>>()
    val uiState = MutableLiveData<Event<UiState>>()
    val moveToHome = MutableLiveData<Event<Unit>>()
    val moveToFindPasswd = MutableLiveData<Event<Unit>>()
    val moveToSignUp = MutableLiveData<Event<Unit>>()

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
            }
        }
    }

    fun signUp(context: Context) = viewModelScope.launch {
        uiState.emit(UiState.Loading)
        val body = makeLoginBody(
            "1",
            email.value.toString(),
            password.value.toString(),
            AppConstants.getAndroidId(context),
            ""
        )
        val response = loginRepository.requestSignIn(SAPA_KEY, body)
        when (response) {
            is Resource.Success -> {
                uiState.emit(UiState.Success)
                if (response.value.status == "200") {
                    AppConstants.ID = response.value.data.id
                    AppConstants.AUTH_KEY = "Bearer ${response.value.data.authorizationToken}"

                    sharedPreferencesManager.saveLoginDataStore(UserDataStore(AppConstants.AUTH_KEY,AppConstants.ID))
                    sharedPreferencesManager.saveLoginType(1)

                    moveToHome.emit()
                } else {
                    if (response.value.error.message.contains("이메일")) {
                        emailErrorPopup.emit(response.value.error)
                    } else if (response.value.error.message.contains("비밀번호")) {
                        passwordErrorPopup.emit(response.value.error)
                    } else {
                        loginErrorPopup.emit(response.value.error)
                    }
                }
            }
            is Resource.Error -> {
                uiState.emit(UiState.Failure(null))
            }
        }
    }

    fun goToFindPassword() {
        moveToFindPasswd.emit()
    }

    fun goToSignUp() {
        moveToSignUp.emit()
    }

    fun isValidEmail(str: String): Boolean {
        return (!TextUtils.isEmpty(str)) && Pattern.compile(EMAIL_PATTERN).matcher(str).matches()
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

    fun onPasswdTextChanged(text: CharSequence) {
        passwdInputStatus.apply {
            if (text.length > 0) {
                value = true
            } else {
                value = false
            }
        }
    }

    fun onInputPasswdClear() {
        passwdClear.value = true
    }
}