package com.kuymakov.chat.data.repositories

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.kuymakov.chat.base.extensions.*
import com.kuymakov.chat.base.firebase.exceptionsWrapper
import com.kuymakov.chat.base.firebase.handleException
import com.kuymakov.chat.base.mappers.toMap
import com.kuymakov.chat.data.UserPresenceHandler
import com.kuymakov.chat.base.network.Success
import com.kuymakov.chat.domain.network.request.ChatRequest
import com.kuymakov.chat.data.toChat
import com.kuymakov.chat.di.IoDispatcher
import com.kuymakov.chat.domain.models.GroupChat
import com.kuymakov.chat.domain.repositories.ChatRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : ChatRepository {
    private val chatCollection = db.collection("chats")

    override suspend fun createChat(chat: ChatRequest) = withContext(dispatcher) {
        exceptionsWrapper {
            val chatMap = chat.toMap().toMutableMap()
            val userRefs = chat.users?.map { db.document("users/${it}") }
                ?: throw IllegalArgumentException()
            chatMap["users"] = userRefs
            val image = chat.image
            if (image != null) {
                val imageUrl = storage.addImage(chat.image)
                chatMap["imageUrl"] = imageUrl
            }
            val chatResult = chatCollection.addAndAwait(chatMap).getAndAwait().toChat()
            Success(chatResult)
        }
    }

    override suspend fun getOrCreatePrivateChat(userId: String) = withContext(dispatcher) {
        exceptionsWrapper {
            val curUserId = UserPresenceHandler.currentUser?.uid ?: error("Unauthorized")
            val curUserRef = db.document("users/$curUserId")
            val chatDocs =
                chatCollection
                    .whereArrayContains("users", curUserRef)
                    .whereEqualTo("type", "private")
                    .get().await()


            val chatDoc = chatDocs.firstOrNull {
                val users = it.getList<DocumentReference>("users")
                if (userId == curUserId) {
                    users.all { ref -> ref.id == userId }
                } else {
                    users.any { ref -> ref.id == userId }
                }
            }
            if (chatDoc != null) {
                Success(chatDoc.toChat())
            } else {
                createChat(ChatRequest(type = "private", users = listOf(curUserId, userId)))
            }
        }
    }

    override suspend fun addUserToGroup(chatId: String, userId: String) = withContext(dispatcher) {
        exceptionsWrapper {
            val userRef = db.document("users/$userId")
            val chat = chatCollection.document(chatId).getAndAwait().toChat()
            if (chat is GroupChat) {
                chatCollection.document(chatId)
                    .updateAndAwait("users", FieldValue.arrayUnion(userRef))
            }
            Success(Unit)
        }
    }


    override suspend fun getChats() = callbackFlow {
        val curUserId = UserPresenceHandler.currentUser?.uid ?: error("Unauthorized")
        val userRef = db.document("users/$curUserId")
        val chatDocs = chatCollection.whereArrayContains("users", userRef)
        val callback = chatDocs.addSnapshotListener { value, ex ->
            CoroutineScope(dispatcher).launch {
                if (ex != null) {
                    trySend(handleException(ex))
                    return@launch
                }
                val docs = value?.documents ?: return@launch
                val chats = docs.mapAsyncAndAwait { it.toChat() }
                trySend(Success(chats))
            }
        }

        awaitClose {
            callback.remove()
        }
    }.flowOn(dispatcher)
}

