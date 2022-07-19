package ai.comake.petping.ui.etc.mission_pet

import ai.comake.petping.MutableEventFlow
import ai.comake.petping.asEventFlow
import ai.comake.petping.util.Coroutines
import androidx.lifecycle.ViewModel

/**
 * android-petping-2
 * Class: MissionPetGuideViewModel
 * Created by cliff on 2022/07/13.
 *
 * Description:
 */
class MissionPetGuideViewModel : ViewModel() {

    private val _eventFlow = MutableEventFlow<MissionPetGuideEvent>()
    val eventFlow = _eventFlow.asEventFlow()


    fun goToHome() {
        event(MissionPetGuideEvent.MoveToHome)
    }

    fun goToSetting() {
        event(MissionPetGuideEvent.MoveToGoSetting)
    }

    private fun event(event: MissionPetGuideEvent) = Coroutines.main(this) {
        _eventFlow.emit(event)
    }

    sealed class MissionPetGuideEvent {
        object MoveToHome : MissionPetGuideEvent()
        object MoveToGoSetting : MissionPetGuideEvent()
    }
}