package ai.comake.petping.ui.etc.welcome_kit

import ai.comake.petping.AppConstants
import ai.comake.petping.Event
import ai.comake.petping.UiState
import ai.comake.petping.api.Resource
import ai.comake.petping.data.repository.UserDataRepository
import ai.comake.petping.data.vo.ErrorResponse
import ai.comake.petping.emit
import ai.comake.petping.util.getErrorBodyConverter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * android-petping-2
 * Class: WelcomeKitGuideViewModel
 * Created by cliff on 2022/02/23.
 *
 * Description:
 */
@HiltViewModel
class WelcomeKitGuideViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var userDataRepository: UserDataRepository

    val welcomeKitApplyErrorPopup = MutableLiveData<Event<ErrorResponse>>()
    val moveToWelcomeKitApply = MutableLiveData<Event<String>>()
    val moveToMissionPet = MutableLiveData<Event<Unit>>()
    val uiState = MutableLiveData<Event<UiState>>()

    fun goToWelcomekit() = viewModelScope.launch {
        uiState.emit(UiState.Loading)
        val response = userDataRepository.getWelcomeKitApply(
            AppConstants.AUTH_KEY,
            AppConstants.ID
        )
        when (response) {
            is Resource.Success -> {
                uiState.emit(UiState.Success)
                val data = response.value.data
                moveToWelcomeKitApply.emit(data.url)
            }
            is Resource.Failure -> {
                uiState.emit(UiState.Failure(null))
                response.errorBody?.let { errorBody ->
                    val errorResponse = getErrorBodyConverter().convert(errorBody)!!
                    welcomeKitApplyErrorPopup.emit(errorResponse)
                }
            }
        }
    }

    fun goToMissionPet() {
        moveToMissionPet.emit()
    }
}