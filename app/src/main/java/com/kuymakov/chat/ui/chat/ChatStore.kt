package com.kuymakov.chat.ui.chat

import androidx.navigation.NavDirections
import com.kuymakov.chat.R
import com.kuymakov.chat.base.mvi.DefaultStore
import com.kuymakov.chat.base.mvi.Store
import com.kuymakov.chat.base.network.onSuccess
import com.kuymakov.chat.domain.models.Message
import com.kuymakov.chat.domain.models.MessageItem
import com.kuymakov.chat.domain.network.request.MessageRequest
import com.kuymakov.chat.domain.repositories.MessageRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope

class ChatStore @AssistedInject constructor(
    private val messagesRepo: MessageRepository,
    @Assisted coroutineScope: CoroutineScope
) : Store<ChatStore.State, ChatStore.Action, ChatStore.Event> by DefaultStore(
    initialState = State(
        isLoading = true,
        sendButtonResId = R.drawable.ic_keyboard_voice_24,
    ),
    scope = coroutineScope,
    onAction = { action ->
        when (action) {
            is Action.CreateMessage -> execute {
                messagesRepo.createMessage(action.message)
            }
            is Action.GetMessages -> execute {
                messagesRepo.getMessages(action.chatId).onSuccess {
                    reduce {
                        copy(messages = it, isLoading = false, hasError = false)
                    }
                }
            }
            is Action.ChangeMessageInput -> reduce {
                copy(
                    sendButtonResId = if (action.isEmpty) R.drawable.ic_keyboard_voice_24 else R.drawable.ic_send_24
                )
            }
            is Action.DeleteMessages -> execute {
                reduce {
                    copy(messages = state.messages.filter { !action.messages.contains(it.id) })
                }
                messagesRepo.deleteMessages(action.chatId, action.messages)
            }
            is Action.SelectMessages -> reduce {
                val newMessages = state.messages.map {
                    if (it is Message && action.messages.contains(it.id)) it.copy(isSelected = !it.isSelected)
                    else it
                }
                copy(messages = newMessages)
            }
        }
    }) {

    data class State(
        val messages: List<MessageItem> = emptyList(),
        val isLoading: Boolean = false,
        val hasNoInternet: Boolean = false,
        val hasError: Boolean = false,
        val sendButtonResId: Int = 0,
        val newMessagesCount: Int = 0

    )

    sealed class Action {
        data class GetMessages(val chatId: String) : Action()
        data class DeleteMessages(val chatId: String, val messages: List<String>) : Action()
        data class CreateMessage(val message: MessageRequest) : Action()
        data class ChangeMessageInput(val isEmpty: Boolean): Action()
        data class SelectMessages(val messages: List<String>) : Action()
    }

    sealed class Event {
        data class Navigation(val directions: NavDirections) : Event()
    }

    @AssistedFactory
    interface Factory {
        fun create(@Assisted coroutineScope: CoroutineScope): ChatStore
    }
}