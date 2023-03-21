package com.kuymakov.chat.domain.repositories

import com.google.firebase.auth.FirebaseUser
import com.kuymakov.chat.base.network.Response

interface AuthRepository {
    suspend fun login(email: String, password: String): Response<FirebaseUser>
    suspend fun register(
        username: String,
        email: String,
        password: String
    ): Response<FirebaseUser>

    suspend fun logout(): Response<Unit>
    fun userAuthorized(): Boolean
    suspend fun deleteAccount(): Response<Unit>
    val curUser: FirebaseUser?
}