package ai.comake.petping.ui.profile

import ai.comake.petping.Event
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

/**
 * android-petping-2
 * Class: SettingViewModel
 * Created by cliff on 2022/03/23.
 *
 * Description:
 */
class SettingViewModel @Inject constructor() : ViewModel() {

    val tabPosition = MutableLiveData<Event<Int>>()
}