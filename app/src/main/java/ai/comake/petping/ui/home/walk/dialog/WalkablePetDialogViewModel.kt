package ai.comake.petping.ui.home.walk.dialog

import ai.comake.petping.Event
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WalkablePetDialogViewModel @Inject constructor() : ViewModel() {
    val _selectedPetIds = MutableLiveData<Event<ArrayList<Int>>>()
    val selectedPetIds: LiveData<Event<ArrayList<Int>>>
        get() = _selectedPetIds

    val _isOverPetMaxSize = MutableLiveData<Event<Boolean>>()
    val isOverPetMaxSize: LiveData<Event<Boolean>>
        get() = _isOverPetMaxSize
}