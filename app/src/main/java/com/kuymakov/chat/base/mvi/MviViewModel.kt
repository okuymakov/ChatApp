package com.kuymakov.chat.base.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

abstract class MviViewModel<State : Any, Action : Any, Event : Any, Store : com.kuymakov.chat.base.mvi.Store<State, Action, Event>> :
    ViewModel() {
    abstract val store: Store
    private var job: Job? = null
    fun bind(view: MviView<State, Action, Event>) {
        if (job == null) {
            job = viewModelScope.launch {
                view.actions.collect(store::onAction)
            }
        }
    }
}

