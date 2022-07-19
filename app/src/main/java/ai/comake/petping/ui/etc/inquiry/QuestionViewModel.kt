package ai.comake.petping.ui.etc.inquiry

import ai.comake.petping.Event
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * android-petping-2
 * Class: QuestionViewModel
 * Created by cliff on 2022/02/25.
 *
 * Description:
 */
class QuestionViewModel : ViewModel() {

    val tabSelected = MutableLiveData<Event<Int>>()

}