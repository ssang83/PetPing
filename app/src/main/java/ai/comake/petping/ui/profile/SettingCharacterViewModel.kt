package ai.comake.petping.ui.profile

import ai.comake.petping.AppConstants
import ai.comake.petping.Event
import ai.comake.petping.UiState
import ai.comake.petping.api.Resource
import ai.comake.petping.data.repository.PetRepository
import ai.comake.petping.data.vo.PetProfileData
import ai.comake.petping.emit
import ai.comake.petping.util.toGender
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * android-petping-2
 * Class: SettingCharacterViewModel
 * Created by cliff on 2022/03/23.
 *
 * Description:
 */
@HiltViewModel
class SettingCharacterViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var repo: PetRepository

    val moveToEditCharacter = MutableLiveData<Event<Unit>>()
    val uiSetUp = MutableLiveData<Event<Unit>>()

    var petProfileData:PetProfileData? = null

    fun loadData(petId:Int) = viewModelScope.launch {
        val response = repo.getPetProfile(AppConstants.AUTH_KEY, petId, AppConstants.ID)
        when (response) {
            is Resource.Success -> {
                petProfileData = response.value.data
                uiSetUp.emit()
            }
        }
    }

    fun goToEditCharacter() {
        moveToEditCharacter.emit()
    }
}