package com.kuymakov.chat.domain.repositories

import com.kuymakov.chat.domain.models.Message
import com.kuymakov.chat.base.network.Response
import com.kuymakov.chat.domain.models.MessageItem
import com.kuymakov.chat.domain.network.request.MessageRequest
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    suspend fun getMessages(chatId: String): Flow<Response<List<MessageItem>>>
    suspend fun createMessage(message: MessageRequest): Response<Unit>
    suspend fun getLastMessage(chatId: String): Response<Message>
    suspend fun deleteMessages(chatId: String,messages: List<String>): Response<Unit>
}