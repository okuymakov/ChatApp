package com.kuymakov.chat.data

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.kuymakov.chat.base.extensions.*
import com.kuymakov.chat.domain.models.*
import com.kuymakov.chat.domain.network.request.MessageRequest
import java.util.*


fun MessageRequest.toMap(): HashMap<String, Any?> {
    val curUserId = UserPresenceHandler.currentUser?.uid ?: error("unauthorized")
    val fromRef = FirebaseFirestore.getInstance().document("users/${curUserId}")
    return hashMapOf(
        "from" to fromRef,
        "date" to Date(),
        "text" to text
    )
}


fun List<Message>.toMessageItems(): List<MessageItem> {
    val messageItems = mutableListOf<MessageItem>()
    groupBy { msg -> msg.date.toLocalDate() }.forEach { (date, messages) ->
        messageItems.addAll(messages)
        messageItems.add(MessagesGroupDate(date = date))
    }
    return messageItems
}

suspend fun DocumentSnapshot.toMessage(): Message {
    val curUserId = UserPresenceHandler.currentUser?.uid ?: error("unauthorized")
    val from = getRefAsObject<User>("from")
    return Message(
        id = id,
        from = from,
        date = getDate("date")!!.toLocalDateTime(),
        text = getString("text")!!,
        isFromMe = curUserId == from.id
    )
}

suspend fun DocumentSnapshot.toChat(): Chat {
    return when (getString("type")!!) {
        "private" -> {
            val curUserId = UserPresenceHandler.currentUser?.uid ?: error("unauthorized")
            val userRefs = getList<DocumentReference>("users")
            val user = userRefs.firstOrAny { it.id != curUserId }.getAndAwait().toObject<User>()

            PrivateChat(
                id = id,
                username = user.username,
                imageUrl = user.imageUrl,
                status = user.status,
                lastSeen = user.lastSeen
            )
        }
        "group" -> {
            GroupChat(
                id = id,
                title = getString("title")!!,
                imageUrl = getString("imageUrl"),
            )
        }
        else -> {
            error("chat type doesn't exist")
        }
    }
}