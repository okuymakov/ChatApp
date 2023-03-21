package com.kuymakov.chat.base.mvi

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface Store<State : Any, Action : Any, Event : Any> {
    val state: StateFlow<State>
    val events: Flow<Event>
    fun onAction(action: Action)
    fun dispose()
}