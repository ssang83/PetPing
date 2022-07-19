package ai.comake.petping.ui.home.dashboard.tip

import ai.comake.petping.AppConstants
import ai.comake.petping.Event
import ai.comake.petping.api.Resource
import ai.comake.petping.data.repository.AppDataRepository
import ai.comake.petping.data.vo.Tip
import ai.comake.petping.emit
import ai.comake.petping.util.Coroutines
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * android-petping-2
 * Class: TipAllViewModel
 * Created by cliff on 2022/05/20.
 *
 * Description:
 */
@HiltViewModel
class TipAllViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var tipRepo:AppDataRepository

    private val _tipList = MutableLiveData<List<Tip>>()
    val tipList: LiveData<List<Tip>> get() = _tipList

    private val _isScroll = MutableLiveData<Boolean>().apply { value = false }
    val isScroll:LiveData<Boolean> get() = _isScroll

    private val _moveToPingTip= MutableLiveData<Event<String>>()
    val moveToPingTip:LiveData<Event<String>> get() = _moveToPingTip

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if(recyclerView.canScrollVertically(-1)) {
                _isScroll.value = true
            } else {
                _isScroll.value = false
            }
        }
    }

    fun loadData() = Coroutines.main(this) {
        val response = tipRepo.getPingTip(AppConstants.AUTH_KEY)
        when(response) {
            is Resource.Success -> {
                _tipList.value = response.value.data.contents
            }
        }
    }

    fun goToPingTip(url:String) {
        _moveToPingTip.emit(url)
    }
}