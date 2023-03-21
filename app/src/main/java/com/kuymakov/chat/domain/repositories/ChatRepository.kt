package com.kuymakov.chat.domain.repositories

import com.kuymakov.chat.domain.models.Chat
import com.kuymakov.chat.domain.network.request.ChatRequest
import com.kuymakov.chat.base.network.Response
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun getChats(): Flow<Response<List<Chat>>>
    suspend fun createChat(chat: ChatRequest): Response<Chat>
    suspend fun addUserToGroup(chatId: String, userId: String): Response<Unit>
    suspend fun getOrCreatePrivateChat(userId: String): Response<Chat>
}