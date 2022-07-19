package ai.comake.petping.ui.history

import ai.comake.petping.AppConstants.AUTH_KEY
import ai.comake.petping.api.Resource
import ai.comake.petping.data.repository.WalkRepository
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class WalkRecordViewModel @Inject constructor() : ViewModel() {
    @Inject
    lateinit var walkRepository: WalkRepository

    val comment = MutableStateFlow("")
    val walkTime = MutableStateFlow("")
    val walkDistance = MutableStateFlow("")
    val markingCount = MutableStateFlow("")
    val markingDetail = MutableStateFlow("")
    val rewardPoint = MutableStateFlow("")
    val isShowRewardView = MutableStateFlow(false)

    var isProgress = MutableLiveData<Boolean>()
    var pictureList: ArrayList<String> = arrayListOf()

    fun walkFinishRecord(
        walkId: Int,
        reviewBody: MultipartBody.Part,
        fileBody: List<MultipartBody.Part>
    ) {
        if(isProgress.value == true) return
        viewModelScope.launch() {
            isProgress.value = true
            viewModelScope.launch {
                val response = walkRepository.walkFinishRecord(
                    AUTH_KEY, walkId, reviewBody, fileBody
                )
                isProgress.value = false
                when (response) {
                    is Resource.Success -> {

                    }
                    else -> {
                        //Do Nothing
                    }
                }
            }
        }
    }
}