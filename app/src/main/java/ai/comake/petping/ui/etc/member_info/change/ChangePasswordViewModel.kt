package ai.comake.petping.ui.etc.member_info.change

import ai.comake.petping.*
import ai.comake.petping.api.Resource
import ai.comake.petping.data.repository.UserDataRepository
import ai.comake.petping.util.PASSWORD_PATTERN
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
 * Class: ChangePasswordViewModel
 * Created by cliff on 2022/03/07.
 *
 * Description:
 */
@HiltViewModel
class ChangePasswordViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var userDataRepository: UserDataRepository

    val password = MutableLiveData<String>().apply { value = "" }
    val confirm = MutableLiveData<String>().apply { value = "" }

    val passwdValidation = MutableLiveData<Boolean>().apply { value = true }
    val passwdInputStatus = MutableLiveData<Boolean>().apply { value = false }
    val passwdClear = MutableLiveData<Boolean>().apply { value = false }
    val passwdFocusHintVisible = MutableLiveData<Boolean>().apply { value = false }
    val passwdLineStatus = MutableLiveData<Boolean>().apply { value = false }
    val passwdConfirmValidation = MutableLiveData<Boolean>().apply { value = true }
    val passwdConfirmInputStatus = MutableLiveData<Boolean>().apply { value = false }
    val passwdConfirmClear = MutableLiveData<Boolean>().apply { value = false }
    val passwdConfirmFocusHintVisible = MutableLiveData<Boolean>().apply { value = false }
    val passwdConfirmLineStatus = MutableLiveData<Boolean>().apply { value = false }

    val uiState = MutableLiveData<Event<UiState>>()
    val showSuccessPopup = MutableLiveData<Event<Unit>>()

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
                    }
                } else {
                    value = true
                }
            }

            password.value = password.value!!.replace(" ", "")
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable?) {}
    }

    val passwordFocusListener = object : View.OnFocusChangeListener {
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

            confirm.value = confirm.value!!.replace(" ", "")
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable?) {}
    }

    val passwordConfirmFocusListener = object : View.OnFocusChangeListener {
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

    fun isValid(password: String, confirm: String): Boolean {
        return (Pattern.compile(PASSWORD_PATTERN).matcher(password).matches() && confirm == password)
    }

    fun onInputPasswdClear() {
        passwdClear.value = true
    }

    fun onInputPasswdConfirmClear() {
        passwdConfirmClear.value = true
    }

    /**
     * 회원정보 변경
     */
    fun goToNext() = viewModelScope.launch {
        uiState.emit(UiState.Loading)
        val body = makeChangeMemberInfoBody(password.value.toString())
        val response =
            userDataRepository.setMemberInfo(AppConstants.AUTH_KEY, AppConstants.ID, body)
        when (response) {
            is Resource.Success -> {
                uiState.emit(UiState.Success)
                showSuccessPopup.emit()
            }
            else -> uiState.emit(UiState.Failure(null))
        }
    }
}