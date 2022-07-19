package ai.comake.petping.ui.etc.member_info.change

import ai.comake.petping.*
import ai.comake.petping.api.Resource
import ai.comake.petping.data.repository.UserDataRepository
import ai.comake.petping.util.EMAIL_PATTERN
import ai.comake.petping.util.getErrorBodyConverter
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject

/**
 * android-petping-2
 * Class: ChangeEmailViewModel
 * Created by cliff on 2022/03/04.
 *
 * Description:
 */
@HiltViewModel
class ChangeEmailViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var userDataRepository: UserDataRepository

    private val _isDuplicate = MutableLiveData<Boolean>().apply { value = true }
    val isDuplicate:LiveData<Boolean> get() = _isDuplicate

    private val _description = MutableLiveData<String>()
    val description:LiveData<String> get() = _description

    private val _hint = MutableLiveData<String>()
    val hint:LiveData<String> get() = _hint

    val errorText = MutableLiveData<String>()
    val errorEnable = MutableLiveData<Boolean>().apply { value = false }
    val email = MutableLiveData<String>().apply { value = "" }
    val showSuccessPopup = MutableLiveData<Event<Unit>>()
    val uiState = MutableLiveData<Event<UiState>>()

    var focusHint = ""
    var helperText = ""

    val textWatcherListener = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s!!.isNotEmpty() && Pattern.compile(EMAIL_PATTERN).matcher(s.toString())
                    .matches().not()
            ) {
                errorEnable.value = true
                errorText.value = "잘못된 주소 형식입니다."
            } else {
                errorEnable.value = false
            }
        }

        override fun afterTextChanged(s: Editable?) {
            if (errorEnable.value == false) {
                checkEmailDuplicate(s.toString())
            }
        }
    }

    fun init(_email: String) {
        email.value = _email

        if (_email.isNotEmpty()) {
            _description.value = "수정할 수 있어요"
            _hint.value = ""
        } else {
            _description.value = "등록할 수 있어요"
            _hint.value = "이메일 주소를 입력하세요"
        }
    }

    fun sendAuthEmail() = viewModelScope.launch {
        uiState.emit(UiState.Loading)
        var isEmailRegistered = false
        val body = makeChangeMemberInfoBody(email.value.toString())
        val response =
            userDataRepository.setMemberInfo(AppConstants.AUTH_KEY, AppConstants.ID, body)
        when (response) {
            is Resource.Success -> {
                uiState.emit(UiState.Success)
                isEmailRegistered = true
            }
            else -> uiState.emit(UiState.Failure(null))
        }

        if (isEmailRegistered) {
            val authBody = makeAuthMailBody(email.value.toString())
            val response1 = userDataRepository.sendEmailAuth(AppConstants.AUTH_KEY, authBody)
            when (response1) {
                is Resource.Success -> {
                    uiState.emit(UiState.Success)
                    showSuccessPopup.emit()
                }
                else -> uiState.emit(UiState.Failure(null))
            }
        }
    }

    fun checkEmailDuplicate(_email:String) {
        _isDuplicate.value = true
        email.value = _email
        viewModelScope.launch {
            val response = userDataRepository.requestHasDuplicateEmail(
                AppConstants.AUTH_KEY,
                _email
            )
            when (response) {
                is Resource.Success -> _isDuplicate.value = false
                is Resource.Failure -> {
                    _isDuplicate.value = true
                    response.errorBody?.let { errorBody ->
                        val errorResponse = getErrorBodyConverter().convert(errorBody)!!
                        if (errorResponse.code == "C3000") {
                            errorEnable.value = true
                            errorText.value = "다른 회원이 사용하는 이메일 주소입니다."
                        }
                    }
                }
            }
        }
    }
}