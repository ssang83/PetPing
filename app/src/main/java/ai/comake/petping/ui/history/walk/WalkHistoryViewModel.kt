package ai.comake.petping.ui.history.walk

import ai.comake.petping.AppConstants
import ai.comake.petping.Event
import ai.comake.petping.UiState
import ai.comake.petping.api.Resource
import ai.comake.petping.data.repository.WalkRepository
import ai.comake.petping.data.vo.WalkRecord
import ai.comake.petping.emit
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * android-petping-2
 * Class: WalkHistoryViewModel
 * Created by cliff on 2022/02/24.
 *
 * Description:
 */
@HiltViewModel
class WalkHistoryViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var walkRepository: WalkRepository

    val walkRecordList = MutableLiveData<List<WalkRecord>>()
    val isPublic = MutableLiveData<Boolean>().apply { value = true }
    val isDelete = MutableLiveData<Boolean>()
    val selectPage = MutableLiveData<String>().apply { value = "" }
    val scrollTopFlag = MutableLiveData<Boolean>().apply { value = false }
    val viewMode = MutableLiveData<String>()

    val showImagePopup = MutableLiveData<Event<String>>()
    val showDeletePopup = MutableLiveData<Event<Int>>()
    val moveToRoute = MutableLiveData<Event<Int>>()
    val uiState = MutableLiveData<Event<UiState>>()
    val appBarScroll = MutableLiveData<Event<Unit>>()
    val refreshHistory = MutableLiveData<Event<Unit>>()

    fun deleteWalkRecord(walkId : Int) = viewModelScope.launch {
        uiState.emit(UiState.Loading)
        val response = walkRepository.deleteWalkRecord(AppConstants.AUTH_KEY, walkId)
        when (response) {
            is Resource.Success -> {
                uiState.emit(UiState.Success)
                refreshHistory.emit()
            }
            else -> uiState.emit(UiState.Failure(null))
        }
    }

    fun delete(walkId: Int) {
        showDeletePopup.emit(walkId)
    }

    fun showPopupImage(imageUrl:String) {
        showImagePopup.emit(imageUrl)
    }

    fun goToRoute(petId:Int) {
        moveToRoute.emit(petId)
    }

    fun scrollTop() {
        scrollTopFlag.value = true
        appBarScroll.emit()
    }
}