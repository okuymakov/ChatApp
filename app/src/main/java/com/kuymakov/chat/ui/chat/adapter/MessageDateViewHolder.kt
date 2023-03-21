package com.kuymakov.chat.ui.chat.adapter

import com.kuymakov.chat.base.extensions.format
import com.kuymakov.chat.base.recyclerview.BaseViewHolder
import com.kuymakov.chat.databinding.MessageDateBinding
import com.kuymakov.chat.domain.models.MessagesGroupDate

class MessageDateViewHolder(binding: MessageDateBinding) :
    BaseViewHolder<MessagesGroupDate?>(binding.root) {
    private val dateView = binding.messageDate

    override fun onBind(item: MessagesGroupDate?) {
        dateView.text = item?.date?.format("d MMMM")
    }

}