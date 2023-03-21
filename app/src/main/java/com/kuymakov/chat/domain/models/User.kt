package com.kuymakov.chat.domain.models

import android.os.Parcelable
import com.kuymakov.chat.base.DateSerializer
import com.kuymakov.chat.base.recyclerview.Item
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import java.util.*


@Serializable
@Parcelize
data class User(
    override val id: String,
    val username: String,
    val email: String? = null,
    val imageUrl: String? = null,
    val status: Status? = null,
    @Serializable(DateSerializer::class)
    val lastSeen: Date? = null
) : Parcelable, Item<String> {
    enum class Status { ONLINE, OFFLINE }
}


