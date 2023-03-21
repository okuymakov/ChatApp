package com.kuymakov.chat.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.withClip
import androidx.core.graphics.withTranslation
import com.kuymakov.chat.R
import com.kuymakov.chat.base.extensions.dp
import com.kuymakov.chat.base.extensions.format
import com.kuymakov.chat.base.extensions.sp
import com.kuymakov.chat.domain.models.Message


class MessageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var status: Status? = null
        set(value) {
            field = value
            statusIcon = status?.toIcon() ?: 0
            invalidate()
        }

    private var messageText: String
    private var messageDate: String
    private var isFromMe: Boolean

    @DrawableRes
    private var avatar: Int

    @DrawableRes
    private var statusIcon: Int


    private var maxWith: Float
    private var messageTextSize: Float
    private var cornerRadius = 17f.dp
    private var statusIconSize: Float = 0f

    private var messagePadding: Float
    private var messageMargin: Float
    private var messageDatePadding: Float
    private var statusIconPadding: Float = 0f


    private val messageTextPaint: TextPaint
    private lateinit var messageTextLayout: StaticLayout
    private val messageDatePaint: Paint
    private val messageDateRect = Rect()
    private val messagePaint: Paint

    private var contentWidth = 0
    private var contentHeight = 0

    fun setMessage(message: Message) {
        isFromMe = message.isFromMe
        messageText = message.text
        messageDate = message.date.format("hh:mm")
        updateValues()
        requestLayout()
        invalidate()
    }

    private fun Status.toIcon(): Int {
        return when (this) {
            Status.SUCCESS -> R.drawable.ic_done_all_24
            Status.LOADING -> R.drawable.ic_loading_24
            Status.ERROR -> R.drawable.ic_error_24
        }
    }

    enum class Status {
        SUCCESS, LOADING, ERROR
    }

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.MessageView,
            0,
            0
        ).apply {
            try {
                messageText = getString(R.styleable.MessageView_messageText) ?: ""
                messageDate = getString(R.styleable.MessageView_messageDate) ?: ""
                avatar = getResourceId(R.styleable.MessageView_avatar, 0)
                messagePadding = getDimension(R.styleable.MessageView_messagePadding, 0f)
                messageMargin = getDimension(R.styleable.MessageView_messageMargin, 0f)
                messageTextSize = getDimension(R.styleable.MessageView_messageTextSize, 16f.sp)
                status = Status.values()[getInt(R.styleable.MessageView_status, 0)]
                statusIcon = status?.toIcon() ?: 0
                maxWith = getDimension(R.styleable.MessageView_maxWidth, Float.MAX_VALUE)
                isFromMe = getBoolean(R.styleable.MessageView_isFromMe, true)
                messageDatePadding = 8f.dp
            } finally {
                recycle()
            }
        }

        messagePaint = Paint().apply {
            style = Paint.Style.FILL
        }

        messageTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = messageTextSize
            color = Color.BLACK
        }

        messageDatePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = (12f / 16f) * messageTextSize
            color = Color.GRAY
        }
        updateValues()
    }

    private fun updateValues() {
        messagePaint.apply {
            color =
                if (isFromMe) context.getColor(R.color.mint) else context.getColor(R.color.gray_light)
        }
        messageDatePaint.getTextBounds(messageDate, 0, messageDate.length, messageDateRect)
        messageTextLayout = buildMessageTextLayout()
        statusIconPadding = if (isFromMe) 8f.dp else 0f
        statusIconSize = if (isFromMe) 12f.dp else 0f
    }

    private fun buildMessageTextLayout(): StaticLayout {
        val curMessageTextWidth = messageTextPaint.measureText(messageText)
        val messageTextWith = if (curMessageTextWidth < maxWith) curMessageTextWidth else maxWith
        return StaticLayout.Builder
            .obtain(messageText, 0, messageText.length, messageTextPaint, messageTextWith.toInt())
            .build()
    }

    private fun measureContent() {
        contentWidth =
            (messageTextLayout.width + messageDateRect.width() + 2 * messagePadding + messageDatePadding + statusIconSize + statusIconPadding).toInt()
        contentHeight =
            (messageTextLayout.height + messageDateRect.height() + 2 * messagePadding).toInt()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureContent()
        val contentHeightWithMargins = contentHeight + 2 * messageMargin.toInt()
        val heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)
        val height = when (MeasureSpec.getMode(heightMeasureSpec)) {
            MeasureSpec.UNSPECIFIED -> contentHeightWithMargins
            MeasureSpec.EXACTLY -> heightSpecSize
            MeasureSpec.AT_MOST -> contentHeightWithMargins.coerceAtMost(heightSpecSize)
            else -> error("Unreachable")
        }
        setMeasuredDimension(widthMeasureSpec, height)
    }


    override fun onDraw(canvas: Canvas) {
        canvas.drawMessage()
    }

    private fun Canvas.drawMessage() {
        val messageBounds =
            if (isFromMe) RectF(
                width - contentWidth - messageMargin,
                messageMargin,
                width - messageMargin,
                height - messageMargin
            )
            else RectF(
                messageMargin,
                messageMargin,
                contentWidth + messageMargin,
                contentHeight + messageMargin
            )

        drawRoundRect(messageBounds, cornerRadius, cornerRadius, messagePaint)
        withTranslation(messageBounds.left + messagePadding, messageBounds.top + messagePadding) {
            drawMessageText()
            drawMessageDateWithStatusIcon()
        }

    }

    private fun Canvas.drawMessageText() {
        messageTextLayout.draw(this, 0f, 0f)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun Canvas.drawMessageDateWithStatusIcon() {
        val dateX = messageTextLayout.width + messageDatePadding
        val dateY = messageTextLayout.height.toFloat()
        drawText(messageDate, dateX, dateY, messageDatePaint)
        if (statusIcon != 0) {
            val iconX = dateX + messageDateRect.width() + statusIconPadding
            val iconY = dateY - messageDateRect.height() / 2 + statusIconSize / 2
            context.getDrawable(statusIcon)?.apply {
                bounds = Rect(
                    iconX.toInt(),
                    (iconY - statusIconSize).toInt(),
                    (iconX + statusIconSize).toInt(),
                    iconY.toInt()
                )
                DrawableCompat.setTint(this, Color.GRAY)
                draw(this@drawMessageDateWithStatusIcon)
            }
        }
    }

    private fun StaticLayout.draw(canvas: Canvas, x: Float, y: Float) {
        canvas.withTranslation(x, y) {
            draw(this)
        }
    }
}