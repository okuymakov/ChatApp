package com.kuymakov.chat.base.extensions

import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder

fun ImageView.load(
    uri: String?,
    build: RequestBuilder<Drawable>.() -> RequestBuilder<Drawable> = { this }
) {
    Glide
        .with(this.context)
        .load(uri)
        .build()
        .into(this)
}

fun ImageView.load(
    uri: Uri?,
    build: RequestBuilder<Drawable>.() -> RequestBuilder<Drawable> = { this }
) {
    Glide
        .with(this.context)
        .load(uri)
        .build()
        .into(this)
}