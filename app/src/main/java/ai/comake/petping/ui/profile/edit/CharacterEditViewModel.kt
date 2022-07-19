package ai.comake.petping.ui.profile.edit

import ai.comake.petping.*
import ai.comake.petping.api.Resource
import ai.comake.petping.data.repository.PetRepository
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import javax.inject.Inject

/**
 * android-petping-2
 * Class: CharacterViewModel
 * Created by cliff on 2022/03/24.
 *
 * Description:
 */
@HiltViewModel
class CharacterEditViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var repo : PetRepository

    val uiState = MutableLiveData<Event<UiState>>()
    val modifySuccess = MutableLiveData<Event<Unit>>()

    var charDefaultType: Int = -1
    var charDefaultColor: String = ""
    var charPatternType: Int = -1
    var charPatternColor: String = ""
    var charBodyType: Int = -1
    var charBodyColor: String = ""
    var petCharId = -1
    var petId = -1

    fun complete() = viewModelScope.launch {
        uiState.emit(UiState.Loading)
        val body = makePetCharacterBody(
            petId = petId,
            charDefaultType = charDefaultType,
            charDefaultColor = charDefaultColor,
            charPatternType = charPatternType,
            charPatternColor = charPatternColor,
            charBodyType = charBodyType,
            charBodyColor = charBodyColor
        )
        val response = repo.modifyPetCharacter(AppConstants.AUTH_KEY, petCharId, body)
        when (response) {
            is Resource.Success -> {
                uiState.emit(UiState.Success)
                modifySuccess.emit()
            }
            else -> uiState.emit(UiState.Failure(null))
        }
    }
}