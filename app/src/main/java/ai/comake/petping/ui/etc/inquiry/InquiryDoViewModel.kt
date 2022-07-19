package ai.comake.petping.ui.etc.inquiry

import ai.comake.petping.AppConstants
import ai.comake.petping.Event
import ai.comake.petping.UiState
import ai.comake.petping.api.Resource
import ai.comake.petping.data.repository.UserDataRepository
import ai.comake.petping.data.vo.InquiryData
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
 * Class: InquiryDoViewModel
 * Created by cliff on 2022/02/22.
 *
 * Description:
 */
@HiltViewModel
class InquiryDoViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var userDataRepository: UserDataRepository

    private val _inquiryItems = MutableLiveData<List<InquiryData>>()
    val inquiryItems: LiveData<List<InquiryData>> get() = _inquiryItems

    val moveToAsk = MutableLiveData<Event<Unit>>()
    val moveToDetail = MutableLiveData<Event<String>>()
    val uiState = MutableLiveData<Event<UiState>>()

    fun loadData() = viewModelScope.launch {
        uiState.emit(UiState.Loading)
        val response = userDataRepository.getInquirys(AppConstants.AUTH_KEY, AppConstants.ID)
        when (response) {
            is Resource.Success -> {
                uiState.emit(UiState.Success)
                _inquiryItems.value = response.value.data
            }
            is Resource.Failure -> uiState.emit(UiState.Failure(null))
        }
    }

    fun goToAskPage() {
        moveToAsk.emit()
    }

    fun goToDetail(url: String) {
        moveToDetail.emit(url)
    }
}