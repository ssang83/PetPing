package ai.comake.petping.ui.etc.member_info.change

import ai.comake.petping.*
import ai.comake.petping.api.Resource
import ai.comake.petping.data.repository.UserDataRepository
import ai.comake.petping.util.EMAIL_PATTERN
import ai.comake.petping.util.getErrorBodyConverter
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
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

    // 인터렉션 관련 사용하는 변수들...
    val emailInputStatus = MutableLiveData<Boolean>().apply { value = false }
    val emailValidation = MutableLiveData<Boolean>().apply { value = true }
    val emailClear = MutableLiveData<Boolean>().apply { value = false }
    val emailLineStatus = MutableLiveData<Boolean>().apply { value = false }
    val emailInitialErrorText = MutableLiveData<Boolean>().apply { value = true }
    val emailHintVisible = MutableLiveData<Boolean>().apply { value = true }

    val email = MutableLiveData<String>().apply { value = "" }
    val showSuccessPopup = MutableLiveData<Event<Unit>>()
    val uiState = MutableLiveData<Event<UiState>>()

    val emailFocusChangeListener = object : View.OnFocusChangeListener {
        override fun onFocusChange(v: View?, hasFocus: Boolean) {
            val str = (v as EditText).text.toString()
            if (hasFocus) {
                emailLineStatus.value = true

                emailInputStatus.apply {
                    if (str.isNotEmpty()) {
                        value = true
                    }
                }
            } else {
                emailLineStatus.value = false
                emailInputStatus.value = false
            }
        }
    }

    val textWatcherListener = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            emailInitialErrorText.value = true

            emailInputStatus.apply {
                if (s?.length!! > 0) {
                    value = true
                } else {
                    value = false
                    emailHintVisible.value = false
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

        override fun afterTextChanged(s: Editable?) {
            checkEmailDuplicate(s.toString())
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
                AppConstants.SAPA_KEY,
                _email
            )
            when (response) {
                is Resource.Success -> _isDuplicate.value = false
                is Resource.Error -> {
                    _isDuplicate.value = true
                    response.errorBody?.let { errorBody ->
                        if (errorBody.code == "C3000") {
                            emailInitialErrorText.value = false
                            emailValidation.value = false
                        }
                    }
                }
            }
        }
    }

    fun onInputEmailClear() {
        emailClear.value = true
        emailHintVisible.value = false
    }
}