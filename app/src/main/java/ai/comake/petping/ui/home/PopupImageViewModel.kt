package ai.comake.petping.ui.home

import ai.comake.petping.Event
import ai.comake.petping.emit
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * android-petping-2
 * Class: PopupImageViewModel
 * Created by cliff on 2022/06/20.
 *
 * Description:
 */
class PopupImageViewModel : ViewModel() {

    val imageUrl = MutableLiveData<String>()
    val moveToWeb = MutableLiveData<Event<String>>()

    var popupUrl = ""

    fun init(url: String, linkUrl: String) {
        imageUrl.value = url
        popupUrl = linkUrl
    }

    fun onPopupClicked() {
        moveToWeb.emit(popupUrl)
    }
}