package ai.comake.petping.ui.etc.inquiry

import ai.comake.petping.AppConstants
import ai.comake.petping.Event
import ai.comake.petping.UiState
import ai.comake.petping.api.Resource
import ai.comake.petping.data.repository.UserDataRepository
import ai.comake.petping.data.vo.NoticeResponseData
import ai.comake.petping.emit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * android-petping-2
 * Class: FAQViewModel
 * Created by cliff on 2022/02/22.
 *
 * Description:
 */
@HiltViewModel
class FAQViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var userDataRepository: UserDataRepository

    private val _faqItems = MutableLiveData<List<NoticeResponseData>>()
    val faqItems: LiveData<List<NoticeResponseData>> get() = _faqItems

    private val _scrollTopFlag = MutableLiveData<Boolean>().apply { value = false }
    val scrollTopFlag: LiveData<Boolean> get() = _scrollTopFlag

    val moveToDetail = MutableLiveData<Event<String>>()
    val appBarScroll = MutableLiveData<Event<Unit>>()
    val uiState = MutableLiveData<Event<UiState>>()

    fun loadData() = viewModelScope.launch {
        uiState.emit(UiState.Loading)
        val response = userDataRepository.getFaqList(AppConstants.AUTH_KEY)
        when (response) {
            is Resource.Success -> {
                uiState.emit(UiState.Success)
                _faqItems.value = response.value.data
            }
            is Resource.Failure -> uiState.emit(UiState.Failure(null))
        }
    }

    fun scrollTop() {
        _scrollTopFlag.value = true
        appBarScroll.emit()
    }

    fun goToFAQPage(url:String) {
        moveToDetail.emit(url)
    }
}