package ai.comake.petping.ui.etc

import ai.comake.petping.*
import ai.comake.petping.api.Resource
import ai.comake.petping.data.repository.PetRepository
import ai.comake.petping.data.vo.ErrorResponse
import ai.comake.petping.util.CERTIFICATION_NAME_PATTERN
import ai.comake.petping.util.EMAIL_PATTERN
import ai.comake.petping.util.NUMBER_PATTERN
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
 * Class: RNSViewModel
 * Created by cliff on 2022/03/21.
 *
 * Description:
 */
@HiltViewModel
class RNSViewModel @Inject constructor() : ViewModel() {

    val registerNumber = MutableLiveData<String>().apply { value = "" }
    val isValidRNS = MutableLiveData<Boolean>().apply { value = true }
    val agree = MutableLiveData<Boolean>().apply { value = false }
    val petOwnName = MutableLiveData<String>().apply { value = "" }
    val petOwnNameValidation = MutableLiveData<Boolean>().apply { value = true }

    // 인터렉션 관련 사용하는 변수들...
    val ownerHelperVisible = MutableLiveData<Boolean>().apply { value = false }
    val ownerInputStatus = MutableLiveData<Boolean>().apply { value = false }
    val ownerClear = MutableLiveData<Boolean>().apply { value = false }
    val ownerFocusHintVisible = MutableLiveData<Boolean>().apply { value = false }
    val ownerLineStatus = MutableLiveData<Boolean>().apply { value = false }
    val rnsHelperVisible = MutableLiveData<Boolean>().apply { value = false }
    val rnsInputStatus = MutableLiveData<Boolean>().apply { value = false }
    val rnsClear = MutableLiveData<Boolean>().apply { value = false }
    val rnsFocusHintVisible = MutableLiveData<Boolean>().apply { value = false }
    val rnsLineStatus = MutableLiveData<Boolean>().apply { value = false }

    val showRNSSaveErrorPopup = MutableLiveData<Event<ErrorResponse>>()
    val moveToMissionPetScreen = MutableLiveData<Event<Unit>>()
    val moveToOutLink = MutableLiveData<Event<Unit>>()
    val uiState = MutableLiveData<Event<UiState>>()

    var petId = -1

    @Inject
    lateinit var petRepository: PetRepository

    /**
     * 15자리 입력 시 버튼 활성화
     */
    val familyCodeEditTextWatcher = object : TextWatcher {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            rnsInputStatus.apply {
                if (s?.length!! > 0) {
                    value = true
                } else {
                    value = false
                }
            }
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable?) {
            if (s?.toString()?.length!! > 0) {
                if (Pattern.compile(NUMBER_PATTERN).matcher(registerNumber.value.toString()).matches()) {
                    isValidRNS.value = true
                } else {
                    isValidRNS.value = false
                    rnsHelperVisible.value = false
                }
            } else {
                isValidRNS.value = true
                rnsHelperVisible.value = true
            }
        }
    }

    val ownerFocusChangedListener = object : View.OnFocusChangeListener {
        override fun onFocusChange(v: View?, hasFocus: Boolean) {
            val str = (v as EditText).text.toString()
            if (hasFocus) {
                ownerFocusHintVisible.value = true
                ownerLineStatus.value = true
                ownerHelperVisible.apply {
                    if (str.isNotEmpty()) {
                        value = false
                    } else {
                        value = true
                    }
                }


                ownerInputStatus.apply {
                    if (str.isNotEmpty()) {
                        value = true
                    } else {
                        value = false
                    }
                }
            } else {
                ownerFocusHintVisible.apply {
                    if (str.isNotEmpty()) {
                        value = true
                    } else {
                        value = false
                    }
                }

                ownerLineStatus.value = false
                ownerInputStatus.value = false
                ownerHelperVisible.value = false
            }
        }
    }

    val rnsFocusChangedListener = object : View.OnFocusChangeListener {
        override fun onFocusChange(v: View?, hasFocus: Boolean) {
            val str = (v as EditText).text.toString()
            if (hasFocus) {
                rnsFocusHintVisible.value = true
                rnsLineStatus.value = true
                rnsHelperVisible.apply {
                    if (str.isNotEmpty()) {
                        value = false
                    } else {
                        value = true
                        isValidRNS.value = true
                    }
                }


                rnsInputStatus.apply {
                    if (str.isNotEmpty()) {
                        value = true
                    } else {
                        value = false
                    }
                }
            } else {
                rnsFocusHintVisible.apply {
                    if (str.isNotEmpty()) {
                        value = true
                    } else {
                        value = false
                    }
                }

                rnsLineStatus.value = false
                rnsInputStatus.value = false
                rnsHelperVisible.value = false
            }
        }
    }

    fun onNameTextChanged(name: CharSequence) {
        petOwnName.value = name.toString()

        ownerInputStatus.apply {
            if (name.length > 0) {
                value = true
            } else {
                value = false
            }
        }

        if (name.length > 0) {
            if (Pattern.compile(CERTIFICATION_NAME_PATTERN).matcher(petOwnName.value!!).matches()) {
                petOwnNameValidation.value = true
            } else {
                petOwnNameValidation.value = false
                ownerHelperVisible.value = false
            }
        } else {
            petOwnNameValidation.value = true
            ownerHelperVisible.value = true
        }
    }

    fun onInputOwnerClear() {
        ownerClear.value = true
    }

    fun onInputRnsClear() {
        rnsClear.value = true
    }

    fun onAgreeClicked() {
        val checkState = (agree.value?:false).not()
        agree.value = checkState
    }

    fun onOutLinkClicked() {
        moveToOutLink.emit()
    }

    fun confirmRegisterNumber() = viewModelScope.launch {
        uiState.emit(UiState.Loading)
        val body = makePetRNSModifyBody(
            registerNumber.value.toString(),
            petOwnName.value.toString()
        )
        val response = petRepository.modifyPetRegisterNumber(
            AppConstants.AUTH_KEY,
            petId,
            body
        )
        when (response) {
            is Resource.Success -> {
                uiState.emit(UiState.Success)
                moveToMissionPetScreen.emit()
            }
            is Resource.Failure -> {
                uiState.emit(UiState.Failure(null))
                response.errorBody?.let { errorBody ->
                    val errorResponse = getErrorBodyConverter().convert(errorBody)!!
                    showRNSSaveErrorPopup.emit(errorResponse)
                }
            }
        }
    }
}