package ai.comake.petping.ui.etc.member_info

import ai.comake.petping.Event
import ai.comake.petping.UiState
import ai.comake.petping.data.vo.PersonalLocationInformationInquiryLog
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * android-petping-2
 * Class: LocationHistoryViewModel
 * Created by cliff on 2022/03/21.
 *
 * Description:
 */
@HiltViewModel
class LocationHistoryViewModel @Inject constructor() : ViewModel() {

    val locationHistoryItems = MutableLiveData<List<PersonalLocationInformationInquiryLog>>()
    val uiState = MutableLiveData<Event<UiState>>()
}