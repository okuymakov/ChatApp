package com.kuymakov.chat.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.kuymakov.chat.base.recyclerview.BaseAdapter
import com.kuymakov.chat.databinding.ChatItemBinding
import com.kuymakov.chat.domain.models.Chat

class ChatsAdapter(onClick: (Chat) -> Unit) :
    BaseAdapter<Chat,String, ChatViewHolder>(onClick = onClick) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ChatItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding)
    }
}