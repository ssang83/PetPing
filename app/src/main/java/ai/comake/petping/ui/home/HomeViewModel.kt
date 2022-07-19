package ai.comake.petping.ui.home

import ai.comake.petping.databinding.FragmentHomeBinding
import ai.comake.petping.databinding.FragmentWalkBinding
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
//    var binding: FragmentHomeBinding? = null

    var title: String = ""
//
//    private val _openWidzet = MutableStateFlow<Event<Boolean>>(Event(false))
//    val openWidzet: StateFlow<Event<Boolean>> = _openWidzet
//
//    private val _openPermission = SingleLiveEvent<Boolean>()
//    val openPermission: LiveData<Boolean> = _openPermission
//
//    private val _openFileUpload = SingleLiveEvent<Boolean>()
//    val openFileUpload: LiveData<Boolean> = _openFileUpload
//
//    private val _openLocationForeground = SingleLiveEvent<Boolean>()
//    val openLocationForeground: LiveData<Boolean> = _openLocationForeground
//
//    fun onWidgetMenuClick() {
//        _openWidzet.value = Event(true)
//    }
//
//    fun onPermissionMenuClick() {
//        _openPermission.value = true
//    }
//
//    fun onFileUploadMenuClick() {
//        _openFileUpload.value = true
//    }
//
//    fun onLocationForegroundMenuClick() {
//        _openLocationForeground.value = true
//    }
}