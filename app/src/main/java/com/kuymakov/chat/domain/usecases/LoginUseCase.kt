package com.kuymakov.chat.domain.usecases

import com.kuymakov.chat.base.network.Failure
import com.kuymakov.chat.base.network.Response
import com.kuymakov.chat.base.network.Success
import com.kuymakov.chat.domain.repositories.AuthRepository
import com.kuymakov.chat.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepo: AuthRepository,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(email: String, password: String): Response<Unit> {
        return withContext(dispatcher) {
            when (val res = authRepo.login(email, password)) {
                is Success -> return@withContext Success(Unit)
                is Failure -> return@withContext res
            }
        }
    }
}