package com.kuymakov.chat.base.ui.avatar

import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.text.TextPaint


class LetterAvatar(
    color: Int,
    var textColor: Int = Color.WHITE,
    var padding: Float = 0.5f,
    var letters: String,
) : ColorDrawable(color) {


    private val textPaint: Paint
    private val textRect: Rect

    init {
        textPaint = textPaint()
        textRect = Rect()
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        val height = bounds.height().toFloat()
        val width = bounds.width().toFloat()

        var size = 0f
        val widthBound = width * (1 - padding)
        val heightBound = height * (1 - padding)
        do {
            textPaint.textSize = ++size
            textPaint.getTextBounds(letters, 0, letters.length, textRect)

        } while (textRect.height() < heightBound && textPaint.measureText(letters) < widthBound)

        val x = width / 2f - textRect.width() / 2f - textRect.left
        val y = height / 2f + textRect.height() / 2f - textRect.bottom

        canvas.drawText(letters, x, y, textPaint)
    }


    private fun textPaint(): TextPaint {
        val textPaint = TextPaint()
        textPaint.isAntiAlias = true
        textPaint.color = textColor
        textPaint.typeface = Typeface.DEFAULT_BOLD
        return textPaint
    }
}