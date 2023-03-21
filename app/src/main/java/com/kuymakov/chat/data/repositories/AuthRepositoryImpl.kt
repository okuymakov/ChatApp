package com.kuymakov.chat.data.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.kuymakov.chat.base.firebase.exceptionsWrapper
import com.kuymakov.chat.base.firebase.loginExceptionsWrapper
import com.kuymakov.chat.base.firebase.registerExceptionsWrapper
import com.kuymakov.chat.base.network.Response
import com.kuymakov.chat.base.network.Success
import com.kuymakov.chat.domain.repositories.AuthRepository
import com.kuymakov.chat.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject


class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : AuthRepository {

    override suspend fun login(email: String, password: String): Response<FirebaseUser> {
        return withContext(dispatcher) {
            loginExceptionsWrapper {
                val res = auth.signInWithEmailAndPassword(email, password).await()
                Success(res.user!!)
            }
        }
    }

    override suspend fun register(
        username: String,
        email: String,
        password: String
    ) = withContext(dispatcher) {
        registerExceptionsWrapper {
            val res = auth.createUserWithEmailAndPassword(email, password).await()
            res.user!!.updateProfile(
                UserProfileChangeRequest.Builder().setDisplayName(username).build()
            ).await()
            Success(res.user!!)
        }
    }


    override suspend fun logout() = withContext(dispatcher) {
        exceptionsWrapper {
            auth.signOut()
            Success(Unit)
        }
    }

    override fun userAuthorized(): Boolean {
        return auth.currentUser != null
    }

    override val curUser: FirebaseUser? get() = auth.currentUser

    override suspend fun deleteAccount() = withContext(dispatcher) {
        exceptionsWrapper {
            auth.currentUser?.delete()?.await()
            Success(Unit)
        }
    }
}