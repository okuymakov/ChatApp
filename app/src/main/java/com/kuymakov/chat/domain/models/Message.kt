package com.kuymakov.chat.domain.models

import com.kuymakov.chat.base.recyclerview.Item
import java.time.LocalDate
import java.time.LocalDateTime

sealed interface MessageItem : Item<String>

data class Message(
    override val id: String,
    val from: User? = null,
    val date: LocalDateTime,
    val text: String,
    val isFromMe: Boolean,
    val status: Status? = null,
    val isSelected: Boolean = false
) : MessageItem {
    enum class Status {
        LOADING, ERROR, SUCCESS
    }
}

data class MessagesGroupDate(
    val date: LocalDate,
    override val id: String = date.toString()
) : MessageItem


