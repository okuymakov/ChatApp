package com.kuymakov.chat.data.repositories

import android.content.res.Resources.NotFoundException
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.kuymakov.chat.base.extensions.lastOrNull
import com.kuymakov.chat.base.extensions.mapAsyncAndAwait
import com.kuymakov.chat.base.firebase.exceptionsWrapper
import com.kuymakov.chat.base.firebase.handleException
import com.kuymakov.chat.base.network.Response
import com.kuymakov.chat.base.network.Success
import com.kuymakov.chat.data.toMap
import com.kuymakov.chat.data.toMessage
import com.kuymakov.chat.data.toMessageItems
import com.kuymakov.chat.di.IoDispatcher
import com.kuymakov.chat.domain.network.request.MessageRequest
import com.kuymakov.chat.domain.repositories.MessageRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.asDeferred
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MessageRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : MessageRepository {

    private fun messages(chatId: String): CollectionReference {
        return db.collection("chats").document(chatId).collection("messages")
    }

    override suspend fun getLastMessage(chatId: String) = withContext(dispatcher) {
        exceptionsWrapper {
            val messageDoc =
                messages(chatId).lastOrNull() ?: throw NotFoundException("Message not found")
            val message = messageDoc.toMessage()
            Success(message)
        }
    }

    override suspend fun deleteMessages(chatId: String, messages: List<String>) =
        withContext(dispatcher) {
            exceptionsWrapper {
                val msgCol = messages(chatId)
                messages.map {
                    msgCol.document(it).delete().asDeferred()
                }.awaitAll()
                Success(Unit)
            }
        }


    override suspend fun getMessages(chatId: String) = callbackFlow {
        val callback = messages(chatId).orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { value, ex ->
                CoroutineScope(dispatcher).launch {
                    if (ex != null) {
                        trySend(handleException(ex))
                        return@launch
                    }
                    val docs = value?.documents ?: return@launch
                    val messages = docs.mapAsyncAndAwait { it.toMessage() }
                    trySend(Success(messages.toMessageItems()))
                }

            }
        awaitClose {
            callback.remove()
        }
    }.flowOn(dispatcher)

    override suspend fun createMessage(message: MessageRequest): Response<Unit> =
        withContext(dispatcher) {
            exceptionsWrapper {
                messages(message.chatId).document(message.id).set(message.toMap()).await()
                Success(Unit)
            }
        }


}