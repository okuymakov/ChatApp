package com.kuymakov.chat.domain.models

import android.os.Parcelable
import com.kuymakov.chat.base.recyclerview.Item

interface Chat : Parcelable,Item<String> {
    override val id: String
    val lastMessage: Message?
    val imageUrl: String?
    fun with(lastMessage: Message?): Chat
}