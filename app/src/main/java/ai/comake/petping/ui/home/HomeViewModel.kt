package ai.comake.petping.ui.home

import ai.comake.petping.databinding.FragmentHomeBinding
import ai.comake.petping.databinding.FragmentWalkBinding
import ai.comake.petping.ui.home.walk.service.LocationUpdatesService
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
    val isStartWalk = LocationUpdatesService._isStartWalk.asLiveData()
}