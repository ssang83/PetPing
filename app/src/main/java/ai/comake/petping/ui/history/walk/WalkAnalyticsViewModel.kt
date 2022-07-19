package ai.comake.petping.ui.history.walk

import ai.comake.petping.AppConstants
import ai.comake.petping.Event
import ai.comake.petping.api.Resource
import ai.comake.petping.data.repository.WalkRepository
import ai.comake.petping.data.vo.WalkStatsData
import ai.comake.petping.emit
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * android-petping-2
 * Class: WalkAnalyticsViewModel
 * Created by cliff on 2022/02/24.
 *
 * Description:
 */
@HiltViewModel
class WalkAnalyticsViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var walkRepository: WalkRepository

    val setUpGraphData = MutableLiveData<Event<WalkStatsData>>()

    var walkStats : WalkStatsData? = null

    fun loadData(petId: Int) = viewModelScope.launch {
        val response = walkRepository.getWalkStats(AppConstants.AUTH_KEY, petId)
        when (response) {
            is Resource.Success -> {
                walkStats = response.value.data
                setUpGraphData.emit(response.value.data)
            }
        }
    }
}