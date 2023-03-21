package com.kuymakov.chat.domain.repositories

import com.kuymakov.chat.base.network.Response
import com.kuymakov.chat.domain.models.User
import com.kuymakov.chat.domain.network.request.UserRequest

interface UserRepository {
    suspend fun fetchUsers(username: String): Response<List<User>>
    suspend fun getUser(userId: String): Response<User>
    suspend fun getUserByEmail(email: String): Response<User>
    suspend fun createUser(user: UserRequest): Response<Unit>
    suspend fun updateUser(user: UserRequest): Response<Unit>
    suspend fun deleteUser(userId: String): Response<Unit>
}