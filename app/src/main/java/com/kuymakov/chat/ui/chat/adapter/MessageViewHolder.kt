package com.kuymakov.chat.ui.chat.adapter

import android.graphics.Color
import com.kuymakov.chat.R
import com.kuymakov.chat.base.recyclerview.BaseViewHolder
import com.kuymakov.chat.databinding.MessageItemBinding
import com.kuymakov.chat.domain.models.Message
import com.kuymakov.chat.views.MessageView

class MessageViewHolder(binding: MessageItemBinding) : BaseViewHolder<Message?>(binding.root) {
    private val messageView = binding.root

    override fun onBind(item: Message?) {
        if (item != null) {
            messageView.setMessage(item)
            if (item.isSelected) {
                messageView.setBackgroundColor(messageView.context.getColor(R.color.black_20))
            } else {
                messageView.setBackgroundColor(Color.TRANSPARENT)
            }
            item.status?.let {
                messageView.status = when(it) {
                    Message.Status.LOADING -> MessageView.Status.LOADING
                    Message.Status.ERROR -> MessageView.Status.ERROR
                    Message.Status.SUCCESS -> MessageView.Status.SUCCESS
                }
            }
        }
    }
}

