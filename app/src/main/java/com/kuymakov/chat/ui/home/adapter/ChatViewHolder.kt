package com.kuymakov.chat.ui.home.adapter

import com.kuymakov.chat.base.ui.avatar.AvatarGenerator
import com.kuymakov.chat.base.extensions.format
import com.kuymakov.chat.base.extensions.load
import com.kuymakov.chat.base.recyclerview.BaseViewHolder
import com.kuymakov.chat.databinding.ChatItemBinding
import com.kuymakov.chat.domain.models.Chat
import com.kuymakov.chat.domain.models.GroupChat
import com.kuymakov.chat.domain.models.PrivateChat

class ChatViewHolder(binding: ChatItemBinding) : BaseViewHolder<Chat>(binding.root) {
    private val imageView = binding.avatar
    private val titleView = binding.title
    private val dateView = binding.date
    private val lastMessageView = binding.lastMessage

    override fun onBind(item: Chat) {
        with(item) {
            var avatarLetters = ""
            when (this) {
                is PrivateChat -> {
                    titleView.text = username
                    lastMessageView.text = lastMessage?.text
                    avatarLetters = username
                }
                is GroupChat -> {
                    titleView.text = title
                    val message = "${lastMessage?.from?.username}: ${lastMessage?.text}"
                    lastMessageView.text = message
                    avatarLetters = title
                }
            }
            dateView.text = lastMessage?.date?.format("hh:mm")

            imageView.load(imageUrl) {
                placeholder(
                    AvatarGenerator(itemView.context).generate(avatarLetters)
                )
            }
        }
    }

}