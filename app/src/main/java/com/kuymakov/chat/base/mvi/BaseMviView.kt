package com.kuymakov.chat.base.mvi

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.kuymakov.chat.base.extensions.launchOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

abstract class BaseMviView<State : Any, Action : Any, Event : Any>(@LayoutRes layout: Int) :
    Fragment(layout), MviView<State, Action, Event> {

    private val _actions: MutableSharedFlow<Action> = MutableSharedFlow()
    override val actions = _actions.asSharedFlow()

    fun bind(viewModel: MviViewModel<State, Action, Event, out Store<State, Action, Event>>) {
        launchOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.store.events.collect(::onEvent)
        }

        launchOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.store.state.collect {
                render(it)
            }
        }
    }

    override fun dispatch(action: Action) {
        launchOnLifecycle(Lifecycle.State.STARTED) {
            _actions.emit(action)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    protected abstract fun init()
}

interface MviView<State : Any, Action : Any, Event : Any> {
    val actions: Flow<Action>
    fun render(state: State)
    fun onEvent(event: Event)
    fun dispatch(action: Action)
}
