package com.kuymakov.chat.base.extensions

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

inline fun <T> List<T>.firstOrAny(predicate: (T) -> Boolean): T {
    return firstOrNull(predicate) ?: first()
}


suspend fun <T, R> Iterable<T>.mapAsyncAndAwait(transform: suspend (T) -> R): List<R> =
    coroutineScope {
        mapAsync(transform).awaitAll()
    }

suspend fun <T, R> Iterable<T>.mapAsync(transform: suspend (T) -> R): List<Deferred<R>> =
    coroutineScope {
        map { async { transform(it) } }
    }

fun <T> Iterable<T>.replace(oldValue: T, newValue: T): List<T> {
    return map {
        if (it == oldValue) newValue else it
    }
}
