package com.kuymakov.chat.ui.chat


import androidx.lifecycle.viewModelScope
import com.kuymakov.chat.base.mvi.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    storeFactory: ChatStore.Factory,
) : MviViewModel<ChatStore.State, ChatStore.Action, ChatStore.Event, ChatStore>() {
    override val store = storeFactory.create(viewModelScope)
}


