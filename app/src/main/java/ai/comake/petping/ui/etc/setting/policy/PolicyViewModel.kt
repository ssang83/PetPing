package ai.comake.petping.ui.etc.setting.policy

import ai.comake.petping.Event
import ai.comake.petping.emit
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

/**
 * android-petping-2
 * Class: PolicyViewModel
 * Created by cliff on 2022/02/24.
 *
 * Description:
 */
class PolicyViewModel @Inject constructor() : ViewModel() {

    val moveToServicePolicy = MutableLiveData<Event<Unit>>()
    val moveToLocationPolicy = MutableLiveData<Event<Unit>>()
    val moveToPrivacyPolicy = MutableLiveData<Event<Unit>>()

    fun goToServicePolicy() {
        moveToServicePolicy.emit()
    }

    fun goToLocationPolicy() {
        moveToLocationPolicy.emit()
    }

    fun goToPrivacyPolicy() {
        moveToPrivacyPolicy.emit()
    }
}