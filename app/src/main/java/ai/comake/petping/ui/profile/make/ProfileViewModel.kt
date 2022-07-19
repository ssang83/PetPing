package ai.comake.petping.ui.profile.make

import ai.comake.petping.Event
import ai.comake.petping.emit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * android-petping-2
 * Class: ProfileViewModel
 * Created by cliff on 2022/06/08.
 *
 * Description:
 */
class ProfileViewModel  : ViewModel() {

    val needBack = MutableLiveData<Boolean>().apply { value = false }

    val moveToMakeProfile = MutableLiveData<Event<Unit>>()
    val moveToConnectFamily = MutableLiveData<Event<Unit>>()

    fun goToMakeProfile() {
        moveToMakeProfile.emit()
    }

    fun goToConnectFamily() {
        moveToConnectFamily.emit()
    }
}