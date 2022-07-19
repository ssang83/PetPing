package ai.comake.petping.ui.login

import ai.comake.petping.*
import ai.comake.petping.api.Resource
import ai.comake.petping.data.repository.LoginRepository
import ai.comake.petping.data.vo.ErrorResponse
import ai.comake.petping.util.EMAIL_PATTERN
import ai.comake.petping.util.getErrorBodyConverter
import android.content.Context
import android.text.TextUtils
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
    lateinit var loginRepository: LoginRepository

    val email = MutableLiveData<String>().apply { value = "" }
    val password = MutableLiveData<String>().apply { value = "" }

    val emailErrorPopup = MutableLiveData<Event<ErrorResponse>>()
    val passwordErrorPopup = MutableLiveData<Event<ErrorResponse>>()
    val uiState = MutableLiveData<Event<UiState>>()
    val moveToHome = MutableLiveData<Event<Unit>>()
    val moveToFindPasswd = MutableLiveData<Event<Unit>>()
    val moveToSignUp = MutableLiveData<Event<Unit>>()

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
                AppConstants.ID = response.value.data.id
                AppConstants.AUTH_KEY = "Bearer ${response.value.data.authorizationToken}"
                AppConstants.LOGIN_HEADER_IS_VISIBLE = false
                moveToHome.emit()
            }
            is Resource.Failure -> {
                uiState.emit(UiState.Failure(null))
                response.errorBody?.let { errorBody ->
                    val errorResponse = getErrorBodyConverter().convert(errorBody)!!
                    if (errorResponse.message.contains("이메일")) {
                        emailErrorPopup.emit(errorResponse)
                    } else if (errorResponse.message.contains("비밀번호")) {
                        passwordErrorPopup.emit(errorResponse)
                    }
                }
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
}