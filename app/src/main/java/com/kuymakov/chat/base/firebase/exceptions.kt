package com.kuymakov.chat.base.firebase

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.storage.StorageException
import com.kuymakov.chat.base.network.Failure
import com.kuymakov.chat.base.network.LoginFailure
import com.kuymakov.chat.base.network.RegisterFailure
import com.kuymakov.chat.base.network.Response

inline fun <T : Any> exceptionsWrapper(block: () -> Response<T>): Response<T> {
    return try {
        block()
    } catch (ex: Exception) {
        handleException(ex)
    }
}

inline fun <T : Any> loginExceptionsWrapper(block: () -> Response<T>): Response<T> {
    return try {
        block()
    } catch (ex: Exception) {
        handleLoginException(ex)
    }
}

fun handleException(ex: Exception): Failure {
    return when (ex) {
        is FirebaseNetworkException -> Failure.NoInternetError(ex)
        is FirebaseFirestoreException -> Failure.UnknownError(ex)
        is StorageException -> Failure.UnknownError(ex)
        is NoSuchElementException -> Failure.UnknownError(ex)
        is FirebaseTooManyRequestsException -> Failure.TooManyRequestsError(ex)
        else -> Failure.UnknownError(ex)
    }
}

fun handleLoginException(ex: Exception): Failure {
    return when (ex) {
        is FirebaseAuthWeakPasswordException,
        is FirebaseAuthInvalidCredentialsException,
        is FirebaseAuthInvalidUserException -> {
            LoginFailure.IncorrectEmailOrPassword(ex)
        }
        else -> handleException(ex)
    }
}

fun handleRegisterException(ex: Exception): Failure {
    return when (ex) {
        is FirebaseAuthUserCollisionException -> {
            RegisterFailure.UserAlreadyExist(ex)
        }
        else -> handleException(ex)
    }
}

inline fun <T : Any> registerExceptionsWrapper(block: () -> Response<T>): Response<T> {
    return try {
        block()
    } catch (ex: Exception) {
        handleRegisterException(ex)
    }
}