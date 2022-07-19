package ai.comake.petping.ui.etc.member_info.change

import ai.comake.petping.*
import ai.comake.petping.api.Resource
import ai.comake.petping.data.repository.UserDataRepository
import ai.comake.petping.util.PASSWORD_PATTERN
import android.text.Editable
import android.text.TextWatcher
import android.view.View
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

    val uiState = MutableLiveData<Event<UiState>>()
    val showSuccessPopup = MutableLiveData<Event<Unit>>()
    val passwordUiUpdate = MutableLiveData<Event<String>>()
    val passwordHasFocus = MutableLiveData<Event<Boolean>>()
    val passwordConfirmUiUpdate = MutableLiveData<Event<String>>()
    val passwordConfirmHasFocus = MutableLiveData<Event<Boolean>>()

    val passwordTextWatcher = object : TextWatcher {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            s?.let { passwordUiUpdate.emit(it.toString()) }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable?) {}
    }

    val passwordFocusListener = object : View.OnFocusChangeListener {
        override fun onFocusChange(v: View?, hasFocus: Boolean) {
            passwordHasFocus.emit(hasFocus)
        }
    }

    val passwordConfirmTextWatcher = object : TextWatcher {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            s?.let { passwordConfirmUiUpdate.emit(it.toString()) }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable?) {}
    }

    val passwordConfirmFocusListener = object : View.OnFocusChangeListener {
        override fun onFocusChange(v: View?, hasFocus: Boolean) {
            passwordConfirmHasFocus.emit(hasFocus)
        }
    }

    fun isValid(password: String, confirm: String): Boolean {
        return (Pattern.compile(PASSWORD_PATTERN).matcher(password).matches() && confirm == password)
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