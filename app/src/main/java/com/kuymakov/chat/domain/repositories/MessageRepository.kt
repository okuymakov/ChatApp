package com.kuymakov.chat.domain.repositories

import com.kuymakov.chat.domain.models.Message
import com.kuymakov.chat.base.network.Response
import com.kuymakov.chat.domain.network.request.MessageRequest
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface MessageRepository {
    suspend fun getMessages(chatId: String, size: Int, maxDate: LocalDateTime? = null): Response<List<Message>>
    suspend fun listenMessagesUpdates(chatId: String): Flow<Response<Int>>
    suspend fun createMessage(message: MessageRequest): Response<Unit>
    suspend fun getLastMessage(chatId: String): Response<Message>
    suspend fun deleteMessages(chatId: String,messages: List<String>): Response<Unit>
}