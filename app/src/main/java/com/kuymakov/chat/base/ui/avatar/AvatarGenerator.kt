package com.kuymakov.chat.base.ui.avatar

import android.content.Context
import android.graphics.drawable.Drawable
import com.kuymakov.chat.R
import com.kuymakov.chat.base.extensions.color

class AvatarGenerator(context: Context) {
    private val colors = arrayOf(
        context.color(R.color.avatar_gray),
        context.color(R.color.avatar_blue),
        context.color(R.color.avatar_yellow),
        context.color(R.color.avatar_green),
        context.color(R.color.avatar_red),
    )

    fun generate(name: String): Drawable {
        val color = colors[name.hashCode() % colors.size]
        val letters = name.split(" ").take(2).map { it.first() }.joinToString().uppercase()
        return LetterAvatar(color = color, letters = letters)
    }
}