package ai.comake.petping.ui.etc.member_info.change

import ai.comake.petping.*
import ai.comake.petping.api.Resource
import ai.comake.petping.data.repository.UserDataRepository
import ai.comake.petping.data.vo.ErrorResponse
import ai.comake.petping.util.getErrorBodyConverter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * android-petping-2
 * Class: ChangePhoneNumberViewModel
 * Created by cliff on 2022/03/17.
 *
 * Description:
 */
@HiltViewModel
class ChangePhoneNumberViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var userDataRepository: UserDataRepository

    val phoneNumber = MutableLiveData<String>().apply { value = "" }
    val name = MutableLiveData<String>().apply { value = "" }
    val id = MutableLiveData<String>().apply { value = "" }
    var ci = ""

    val moveToCert = MutableLiveData<Event<Unit>>()
    val uiState = MutableLiveData<Event<UiState>>()
    val phoneAuthSuccess = MutableLiveData<Event<Unit>>()
    val phoneAuthFail = MutableLiveData<Event<ErrorResponse>>()

    fun goToChangePhoneNumber() = viewModelScope.launch {
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
            }
            is Resource.Failure -> {
                uiState.emit(UiState.Failure(null))
                response.errorBody?.let { errorBody ->
                    val errorResponse = getErrorBodyConverter().convert(errorBody)!!
                    phoneAuthFail.emit(errorResponse)
                }
            }
        }
    }

    fun goToCertification() {
        moveToCert.emit()
    }
}