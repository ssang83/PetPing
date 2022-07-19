package ai.comake.petping.ui.etc.notice

import ai.comake.petping.AppConstants
import ai.comake.petping.Event
import ai.comake.petping.UiState
import ai.comake.petping.api.Resource
import ai.comake.petping.data.repository.UserDataRepository
import ai.comake.petping.data.vo.ErrorResponse
import ai.comake.petping.data.vo.NoticeResponseData
import ai.comake.petping.emit
import ai.comake.petping.util.getErrorBodyConverter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * android-petping-2
 * Class: NoticeViewModel
 * Created by cliff on 2022/02/22.
 *
 * Description:
 */
@HiltViewModel
class NoticeViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var userDataRepository: UserDataRepository

    private val _noticeItems = MutableLiveData<List<NoticeResponseData>>()
    val noticeItem: LiveData<List<NoticeResponseData>> get() = _noticeItems

    private val _topBtnVisible = MutableLiveData<Boolean>().apply { value = false }
    val topBtnVisible: LiveData<Boolean> get() = _topBtnVisible

    private val _scrollTopFlag = MutableLiveData<Boolean>().apply { value = false }
    val scrollTopFlag: LiveData<Boolean> get() = _scrollTopFlag

    val moveToNoticePage = MutableLiveData<Event<String>>()
    val errorPopup = MutableLiveData<Event<ErrorResponse>>()
    val uiState = MutableLiveData<Event<UiState>>()

    fun loadData() = viewModelScope.launch {
        uiState.emit(UiState.Loading)
        val response = userDataRepository.getNoticeList(AppConstants.AUTH_KEY)
        when (response) {
            is Resource.Success -> {
                uiState.emit(UiState.Success)
                _noticeItems.value = response.value.data!!
            }
            is Resource.Failure -> {
                uiState.emit(UiState.Failure(null))
                response.errorBody?.let { errorBody ->
                    val errorResponse = getErrorBodyConverter().convert(errorBody)!!
                    errorPopup.emit(errorResponse)
                }
            }
        }
    }

    fun scrollTop() {
        _scrollTopFlag.value = true
    }

    fun goToNoticePage(url: String) {
        moveToNoticePage.emit(url)
    }

    fun setTopBtnStatus(status:Boolean) {
        _topBtnVisible.value = status
    }
}