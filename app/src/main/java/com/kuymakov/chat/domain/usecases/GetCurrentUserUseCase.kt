package com.kuymakov.chat.domain.usecases

import android.content.res.Resources.NotFoundException
import com.kuymakov.chat.base.network.Failure
import com.kuymakov.chat.domain.models.User
import com.kuymakov.chat.base.network.Response
import com.kuymakov.chat.domain.repositories.AuthRepository
import com.kuymakov.chat.domain.repositories.UserRepository
import com.kuymakov.chat.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val userRepo: UserRepository,
    private val authRepo: AuthRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(): Response<User> {
        return withContext(dispatcher) {
            val authUser = authRepo.curUser
            val email =
                authUser?.email ?: return@withContext Failure.UnknownError(NotFoundException())
            userRepo.getUserByEmail(email)
        }
    }
}