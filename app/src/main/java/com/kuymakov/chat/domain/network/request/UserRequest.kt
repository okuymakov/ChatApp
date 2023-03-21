package com.kuymakov.chat.domain.network.request

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class UserRequest(
    @Transient val id: String? = null,
    val username: String,
    val email: String? = null,
    @Transient val image: ByteArray? = null,
)