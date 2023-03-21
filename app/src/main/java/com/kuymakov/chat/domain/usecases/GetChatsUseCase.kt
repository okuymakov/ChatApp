package com.kuymakov.chat.domain.usecases

import com.kuymakov.chat.base.network.Failure
import com.kuymakov.chat.base.network.Success
import com.kuymakov.chat.domain.repositories.ChatRepository
import com.kuymakov.chat.domain.repositories.MessageRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetChatsUseCase @Inject constructor(
    private val chatRepo: ChatRepository,
    private val messageRepo: MessageRepository,
) {
    suspend operator fun invoke() = chatRepo.getChats().map {
        when (it) {
            is Success -> {
                val chats = it.data
                val chatsWithLastMessage = chats.map { chat ->
                    val lastMessage = when (val res = messageRepo.getLastMessage(chat.id)) {
                        is Success -> res.data
                        is Failure -> null
                    }
                    chat.with(lastMessage)
                }.filter {chat -> chat.lastMessage != null }
                Success(chatsWithLastMessage)
            }
            is Failure -> it
        }
    }
}