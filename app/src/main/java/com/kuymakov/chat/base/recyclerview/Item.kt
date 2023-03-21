package com.kuymakov.chat.base.recyclerview

interface Item<T> {
    val id: T
    override fun equals(other: Any?): Boolean
}

