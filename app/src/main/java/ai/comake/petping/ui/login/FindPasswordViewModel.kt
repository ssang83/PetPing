package ai.comake.petping.ui.login

import ai.comake.petping.*
import ai.comake.petping.AppConstants.SAPA_KEY
import ai.comake.petping.api.Resource
import ai.comake.petping.data.repository.LoginRepository
import ai.comake.petping.data.vo.ErrorResponse
import ai.comake.petping.util.EMAIL_PATTERN
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
 * Class: FindPasswordViewModel
 * Created by cliff on 2022/03/11.
 *
 * Description:
 */
@HiltViewModel
class FindPasswordViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var loginRepository: LoginRepository

    val email = MutableLiveData<String>().apply { value = "" }
    val emailErrorPopup = MutableLiveData<Event<ErrorResponse>>()
    val emailSuccessPopup = MutableLiveData<Event<Unit>>()
    val moveToLogin = MutableLiveData<Event<Unit>>()
    val uiState = MutableLiveData<Event<UiState>>()

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
}