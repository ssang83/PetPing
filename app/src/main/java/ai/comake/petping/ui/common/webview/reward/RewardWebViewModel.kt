package ai.comake.petping.ui.common.webview.reward

import ai.comake.petping.*
import ai.comake.petping.api.Resource
import ai.comake.petping.data.repository.ShopRepository
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

/**
 * android-petping-2
 * Class: RewardWebViewModel
 * Created by cliff on 2022/02/15.
 *
 * Description:
 */
@HiltViewModel
class RewardWebViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var shopRepository: ShopRepository

    val missionType = MutableLiveData<Int>()
    val missionState = MutableLiveData<Int>()

    private val _showUploadPopUp = MutableLiveData<Event<Unit>>()
    val showUploadPopup:LiveData<Event<Unit>>
        get() = _showUploadPopUp

    private val _uploadSuccessPopup = MutableLiveData<Event<Unit>>()
    val uploadSuccessPopup:LiveData<Event<Unit>>
        get() = _uploadSuccessPopup

    private val _uiState = MutableLiveData<Event<UiState>>()
    val uiState:LiveData<Event<UiState>>
        get() = _uiState

    var missionId = -1

    fun uploadFiles(context: Context, fileList:List<String>) = viewModelScope.launch {
        _uiState.emit(UiState.Loading)
        val body = context.makeMissionFileUploadBody(fileList)
        val memberId = MultipartBody.Part.createFormData("memberId", AppConstants.ID)
        val missionId = MultipartBody.Part.createFormData("missionId", missionId.toString())

        val response = shopRepository.uploadMissionFiles(
            AppConstants.AUTH_KEY,
            memberId,
            missionId,
            body
        )

        when (response) {
            is Resource.Success -> {
                _uiState.emit(UiState.Success)
                _uploadSuccessPopup.emit()
            }
            else -> _uiState.emit(UiState.Failure(null))
        }
    }


    fun upload() {
        _showUploadPopUp.emit()
    }
}