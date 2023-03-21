package com.kuymakov.chat.domain.network.request

import com.kuymakov.chat.domain.models.Message
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
class MessageRequest(
    val id: String,
    val chatId: String,
    val text: String
)

fun MessageRequest.toMessage(status: Message.Status): Message {
    return Message(
        id = id,
        isFromMe = true,
        date = LocalDateTime.now(),
        text = text,
        status = status
    )
}