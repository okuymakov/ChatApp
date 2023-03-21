package com.kuymakov.chat.domain.network.request

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class ChatRequest(
    val id: String? = null,
    val title: String? = null,
    @Transient val image: ByteArray? = null,
    val type: String,
    @Transient val users: List<String>? = null
)


