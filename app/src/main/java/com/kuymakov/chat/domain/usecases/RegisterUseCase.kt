package com.kuymakov.chat.domain.usecases

import com.kuymakov.chat.base.network.Failure
import com.kuymakov.chat.base.network.Response
import com.kuymakov.chat.base.network.Success
import com.kuymakov.chat.domain.network.request.UserRequest
import com.kuymakov.chat.domain.repositories.AuthRepository
import com.kuymakov.chat.domain.repositories.UserRepository
import com.kuymakov.chat.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepo: AuthRepository,
    private val userRepo: UserRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend operator fun invoke(
        username: String,
        email: String,
        password: String
    ): Response<Unit> {
        return withContext(dispatcher) {
            when(val res = authRepo.register(username, email, password)) {
                is Success -> {
                    val authUser = res.data
                    val user = UserRequest(id = authUser.uid, username = username, email = email)
                    userRepo.createUser(user)
                    Success(Unit)
                }
                is Failure -> res
            }
        }
    }
}


