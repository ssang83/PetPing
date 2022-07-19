package ai.comake.petping.ui.etc

import ai.comake.petping.*
import ai.comake.petping.api.Resource
import ai.comake.petping.data.repository.PetRepository
import ai.comake.petping.data.vo.ErrorResponse
import ai.comake.petping.util.CERTIFICATION_NAME_PATTERN
import ai.comake.petping.util.NUMBER_PATTERN
import ai.comake.petping.util.getErrorBodyConverter
import android.text.Editable
import android.text.TextWatcher
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
    val isValidRNS = MutableLiveData<Boolean>().apply { value = false }
    val agree = MutableLiveData<Boolean>().apply { value = false }
    val petOwnName = MutableLiveData<String>().apply { value = "" }
    val petOwnNameValidation = MutableLiveData<Boolean>().apply { value = false }

    val showRNSSaveErrorPopup = MutableLiveData<Event<ErrorResponse>>()
    val moveToMissionPetScreen = MutableLiveData<Event<Unit>>()
    val moveToOutLink = MutableLiveData<Event<Unit>>()
    val uiState = MutableLiveData<Event<UiState>>()
    val helperTextUpdate = MutableLiveData<Event<String>>()

    var petId = -1

    @Inject
    lateinit var petRepository: PetRepository

    /**
     * 15자리 입력 시 버튼 활성화
     */
    val familyCodeEditTextWatcher = object : TextWatcher {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable?) {
            if (registerNumber.value.toString().length == 15 && Pattern.compile(NUMBER_PATTERN).matcher(registerNumber.value.toString()).matches()) {
                isValidRNS.value = true
            } else {
                isValidRNS.value = false
                if (Pattern.compile(NUMBER_PATTERN).matcher(registerNumber.value.toString()).matches()) {
                    helperTextUpdate.emit("15자리의 숫자를 입력해 주세요.")
                }
            }
        }
    }

    fun onNameTextChanged(name: CharSequence) {
        petOwnName.value = name.toString()

        if (Pattern.compile(CERTIFICATION_NAME_PATTERN).matcher(petOwnName.value!!).matches()) {
            petOwnNameValidation.value = true
        } else {
            petOwnNameValidation.value = false
        }
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