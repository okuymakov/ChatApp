package com.kuymakov.chat.domain.models

import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class GroupChat(
    override val id: String,
    val title: String,
    override val imageUrl: String? = null,
    @IgnoredOnParcel
    override val lastMessage: Message? = null,
    val users: List<User>? = null,
) : Chat {
    override fun with(lastMessage: Message?): Chat {
        return copy(lastMessage = lastMessage)
    }
}