package com.kuymakov.chat.base.ui.avatar

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.kuymakov.chat.R

class AvatarGenerator(context: Context) {
    private val colors = arrayOf(
        ContextCompat.getColor(context, R.color.avatar_gray),
        ContextCompat.getColor(context, R.color.avatar_blue),
        ContextCompat.getColor(context, R.color.avatar_yellow),
        ContextCompat.getColor(context, R.color.avatar_green),
        ContextCompat.getColor(context, R.color.avatar_red),
    )

    fun generate(name: String): Drawable {
        val color = colors[name.hashCode() % colors.size]
        val letters = name.split(" ").take(2).map { it.first() }.joinToString().uppercase()
        return LetterAvatar(color = color, letters = letters)
    }
}