package com.kuymakov.chat.domain.models

import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.util.*


@Parcelize
data class PrivateChat(
    override val id: String,
    val username: String,
    override val imageUrl: String? = null,
    @IgnoredOnParcel
    override val lastMessage: Message? = null,
    val status: User.Status? = null,
    val lastSeen: Date? = null
) : Chat {
    override fun with(lastMessage: Message?): Chat {
        return copy(lastMessage = lastMessage)
    }

}

