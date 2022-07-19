package ai.comake.petping.ui.signup

import ai.comake.petping.SingleLiveEvent
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class SignUpViewModel @ViewModelInject constructor() : ViewModel() {
    private val _items = SingleLiveEvent<MutableList<String>>()
    val items: LiveData<MutableList<String>> = _items

    fun loadTasks() {
        val list = mutableListOf("Tab1","Tab2")
        _items.value = list
    }
}