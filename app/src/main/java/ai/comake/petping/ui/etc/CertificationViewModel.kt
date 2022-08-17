package ai.comake.petping.ui.etc

import ai.comake.petping.*
import ai.comake.petping.api.Resource
import ai.comake.petping.data.repository.UserDataRepository
import ai.comake.petping.data.vo.ErrorResponse
import ai.comake.petping.util.CERTIFICATION_NAME_PATTERN
import ai.comake.petping.util.EMAIL_PATTERN
import ai.comake.petping.util.ID_PATTERN
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
 * Class: CertificationViewModel
 * Created by cliff on 2022/03/21.
 *
 * Description:
 */
@HiltViewModel
class CertificationViewModel @Inject constructor() : ViewModel() {

    val name = MutableLiveData<String>().apply { value = "" }
    val id = MutableLiveData<String>().apply { value = "" }
    val phoneNumber= MutableLiveData<String>().apply { value = "" }
    val phoneAuthStatus = MutableLiveData<Boolean>().apply { value = false }

    val phoneAuthFail = MutableLiveData<Event<ErrorResponse>>()
    val phoneAuthSuccess = MutableLiveData<Event<Unit>>()
    val uiState = MutableLiveData<Event<UiState>>()
    val moveToNext = MutableLiveData<Event<Unit>>()
    val moveToCetWeb = MutableLiveData<Event<Unit>>()

    // 인터렉션 관련 사용하는 변수들...
    val nameInputStatus = MutableLiveData<Boolean>().apply { value = false }
    val nameValidation = MutableLiveData<Boolean>().apply { value = true }
    val nameClear = MutableLiveData<Boolean>().apply { value = false }
    val nameFocusHintVisible = MutableLiveData<Boolean>().apply { value = false }
    val nameLineStatus = MutableLiveData<Boolean>().apply { value = false }
    val nameHelperVisible = MutableLiveData<Boolean>().apply { value = false }

    var ci = ""

    val nameFocusChangeListener = object : View.OnFocusChangeListener {
        override fun onFocusChange(v: View?, hasFocus: Boolean) {
            val str = (v as EditText).text.toString()
            if (hasFocus) {
                nameFocusHintVisible.value = true
                nameLineStatus.value = true
                nameHelperVisible.apply {
                    if (str.isNotEmpty()) {
                        value = false
                    } else {
                        value = true
                    }
                }

                nameInputStatus.apply {
                    if (str.isNotEmpty()) {
                        value = true
                    }
                }
            } else {
                nameFocusHintVisible.apply {
                    if (str.isNotEmpty()) {
                        value = true
                    } else {
                        value = false
                    }
                }

                nameLineStatus.value = false
                nameInputStatus.value = false
                nameHelperVisible.value = false
            }
        }
    }

    @Inject
    lateinit var userDataRepository: UserDataRepository

    /**
     * 휴대폰 본인인증 버튼 활성화 조건
     */
    fun isValidText(name: String, id: String, phoneNumber: String): Boolean {
        return Pattern.compile(CERTIFICATION_NAME_PATTERN).matcher(name).matches()
                && id.length == 7 && phoneNumber == ""
    }

    /**
     * 다음 단계로 버튼 활성화 조건
     */
    fun isValid(name: String, id: String, phoneNumber: String, authStatus:Boolean): Boolean {
        return Pattern.compile(CERTIFICATION_NAME_PATTERN).matcher(name).matches() && Pattern.compile(
            ID_PATTERN
        ).matcher(id).matches() && phoneNumber != "" && authStatus
    }

    fun goToCertification() {
        moveToCetWeb.emit()
    }

    fun goToNext() {
        moveToNext.emit()
    }

    fun memberPhoneAuth() = viewModelScope.launch {
        uiState.emit(UiState.Loading)
        val body = makePhoneAuthBody(
            phone = phoneNumber.value.toString().replace("-", ""),
            ci = ci,
            name = name.value.toString(),
            birthAndGender = id.value.toString()
        )
        val response = userDataRepository.setMemberValidation(AppConstants.AUTH_KEY, body)
        when (response) {
            is Resource.Success -> {
                uiState.emit(UiState.Success)
                phoneAuthSuccess.emit()
                phoneAuthStatus.postValue(true)
            }
            is Resource.Failure -> {
                uiState.emit(UiState.Failure(null))
                phoneAuthStatus.postValue(false)
                response.errorBody?.let { errorBody ->
                    val errorResponse = getErrorBodyConverter().convert(errorBody)!!
                    phoneAuthFail.emit(errorResponse)
                }
            }
        }
    }

    fun onNameTextChanged(text: CharSequence) {

        nameInputStatus.apply {
            if (text.length > 0) {
                value = true
            } else {
                value = false
            }
        }

        nameValidation.apply {
            if (text.length > 0) {
                if (Pattern.compile(CERTIFICATION_NAME_PATTERN).matcher(text.toString()).matches()) {
                    value = true
                } else {
                    value = false
                }
            } else {
                value = true
            }
        }
    }

    fun onInputNameClear() {
        nameClear.value = true
    }
}