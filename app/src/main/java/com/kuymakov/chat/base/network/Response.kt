package com.kuymakov.chat.base.network

import kotlinx.coroutines.flow.Flow

sealed interface Response<out T : Any>

data class Success<out T : Any>(val data: T) : Response<T>


sealed interface Failure : Response<Nothing> {
    data class NoInternetError(val exception: Exception) : Failure
    data class UnknownError(val exception: Exception) : Failure
    data class TooManyRequestsError(val exception: Exception) : Failure
}


sealed class LoginFailure : Failure {
    data class IncorrectEmailOrPassword(val exception: Exception) : LoginFailure()
}

sealed class RegisterFailure : Failure {
    data class UserAlreadyExist(val exception: Exception) : RegisterFailure()
}

suspend inline fun <T : Any> Flow<Response<T>>.onSuccess(crossinline block: (T) -> Unit) {
    collect { res ->
        if (res is Success) {
            block(res.data)
        }
    }
}

inline fun <T : Any> Response<T>.onSuccess(block: (T) -> Unit): Response<T> {
    if (this is Success) {
        block(data)
    }
    return this
}

inline fun <T : Any> Response<T>.onFailure(block: (Failure) -> Unit): Response<T> {
    if (this is Failure) {
        block(this)
    }
    return this
}
