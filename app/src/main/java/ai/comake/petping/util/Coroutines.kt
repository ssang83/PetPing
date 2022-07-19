package ai.comake.petping.util

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * android-petping-2
 * Class: Coroutines
 * Created by cliff on 2022/05/10.
 *
 * Description:
 */
object Coroutines {

    //UI contexts
    fun main(work : suspend (() -> Unit)) =
        CoroutineScope(Dispatchers.Main).launch {
            work()
        }

    fun main(viewModel : ViewModel, work : suspend (() -> Unit)) =
        viewModel.viewModelScope.launch(Dispatchers.Main) {
            work()
        }

    fun main(lifecycleOwner: LifecycleOwner, work: suspend () -> Unit) =
        lifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            work()
        }

    // I/O operations
    fun io(work : suspend (() -> Unit)) =
        CoroutineScope(Dispatchers.IO).launch {
            work()
        }

    fun io(viewModel : ViewModel, work : suspend (() -> Unit)) =
        viewModel.viewModelScope.launch(Dispatchers.IO) {
            work()
        }

    fun io(lifecycleOwner: LifecycleOwner, work: suspend () -> Unit) =
        lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            work()
        }

    // Uses heavy CPU computation
    fun default(work : suspend (() -> Unit)) =
        CoroutineScope(Dispatchers.Default).launch {
            work()
        }

    fun default(viewModel : ViewModel, work : suspend (() -> Unit)) =
        viewModel.viewModelScope.launch(Dispatchers.Default) {
            work()
        }

    fun default(lifecycleOwner: LifecycleOwner, work: suspend () -> Unit) =
        lifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
            work()
        }

    // No need to run on specific context
    fun unconfined(work : suspend (() -> Unit)) =
        CoroutineScope(Dispatchers.Unconfined).launch {
            work()
        }

    fun unconfined(viewModel : ViewModel, work : suspend (() -> Unit)) =
        viewModel.viewModelScope.launch(Dispatchers.Unconfined) {
            work()
        }

    fun unconfined(lifecycleOwner: LifecycleOwner, work: suspend () -> Unit) =
        lifecycleOwner.lifecycleScope.launch(Dispatchers.Unconfined) {
            work()
        }
}
