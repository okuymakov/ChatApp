package com.kuymakov.chat.ui.chat

import androidx.navigation.NavDirections
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kuymakov.chat.R
import com.kuymakov.chat.base.mvi.DefaultStore
import com.kuymakov.chat.base.mvi.Store
import com.kuymakov.chat.domain.models.MessageItem
import com.kuymakov.chat.base.network.onSuccess
import com.kuymakov.chat.domain.usecases.GetMessagesUseCase
import com.kuymakov.chat.domain.network.request.MessageRequest
import com.kuymakov.chat.domain.repositories.MessageRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest

class ChatStore @AssistedInject constructor(
    private val messagesRepo: MessageRepository,
    private val getMessages: GetMessagesUseCase,
    @Assisted coroutineScope: CoroutineScope
) : Store<ChatStore.State, ChatStore.Action, ChatStore.Event> by DefaultStore(
    initialState = State(
        isLoading = true,
        sendButtonResId = R.drawable.ic_keyboard_voice_24,
        isVoiceSendButton = false
    ),
    scope = coroutineScope,
    onAction = { action ->
        when (action) {
            is Action.CreateMessage -> execute {
                if (!state.isVoiceSendButton) {
                    messagesRepo.createMessage(action.message)
                }
            }
            is Action.GetMessages -> execute {
                getMessages(action.chatId).cachedIn(this).collectLatest { value ->
                    reduce {
                        copy(messages = value, isLoading = false, hasError = false)
                    }
                }
            }
            is Action.ChangeMessageInputType -> reduce {
                copy(
                    isVoiceSendButton = !state.isVoiceSendButton,
                    sendButtonResId = if (state.isVoiceSendButton) R.drawable.ic_keyboard_voice_24 else R.drawable.ic_send_24
                )
            }
            is Action.ListenMessagesUpdates -> execute {
                messagesRepo.listenMessagesUpdates(action.chatId).onSuccess {
                    //reduce { copy(newMessagesCount = it) }
                }
            }
            is Action.DeleteMessages -> execute {
                messagesRepo.deleteMessages(action.chatId, action.messages)
            }
        }
    }) {

    data class State(
        val messages: PagingData<MessageItem> = PagingData.empty(),
        val isLoading: Boolean = false,
        val hasNoInternet: Boolean = false,
        val hasError: Boolean = false,
        val sendButtonResId: Int = 0,
        val isVoiceSendButton: Boolean = true,
        val newMessagesCount: Int = 0

    )

    sealed class Action {
        data class GetMessages(val chatId: String) : Action()
        data class DeleteMessages(val chatId: String, val messages: List<String>) : Action()
        data class CreateMessage(val message: MessageRequest, ) : Action()

        object ChangeMessageInputType : Action()
        data class ListenMessagesUpdates(val chatId: String) : Action()
    }

    sealed class Event {
        data class Navigation(val directions: NavDirections) : Event()
    }

    @AssistedFactory
    interface Factory {
        fun create(@Assisted coroutineScope: CoroutineScope): ChatStore
    }
}