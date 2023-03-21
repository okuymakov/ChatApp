package com.kuymakov.chat.base.mvi

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DefaultStore<State : Any, Action : Any, Event : Any>(
    initialState: State,
    private val scope: CoroutineScope,
    private val onAction: StoreContext<State, Event>.(Action) -> Unit,
) : Store<State, Action, Event> {
    private var job: Job? = null

    private val _state: MutableStateFlow<State> = MutableStateFlow(initialState)
    override val state = _state.asStateFlow()

    private val _events: Channel<Event> = Channel()
    override val events = _events.receiveAsFlow()


    override fun onAction(action: Action) {
        StoreContext<State, Event>(
            state = _state.value,
            executorScope = scope,
            onEvent = {
                scope.launch {
                    _events.send(it)
                }
            },
            onState = {
                scope.launch {
                    if (_state.value != it) {
                        _state.value = it
                    }
                }
            }
        ).onAction(action)

    }

    override fun dispose() {
        job?.cancel()
    }
}


class StoreContext<State, Event>(
    val state: State,
    private val executorScope: CoroutineScope,
    private val onEvent: ((Event) -> Unit)? = null,
    private val onState: ((State) -> Unit)? = null,
) {
    fun reduce(reducer: State.() -> State) {
        onState?.let { it(state.reducer()) }
    }

    fun publish(event: Event) {
        onEvent?.let { it(event) }
    }

    fun execute(scope: CoroutineScope = executorScope, block: suspend CoroutineScope.() -> Unit) {
        scope.launch {
            block()
        }
    }
}



