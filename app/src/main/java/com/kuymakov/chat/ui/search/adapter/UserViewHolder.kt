package com.kuymakov.chat.ui.search.adapter

import com.kuymakov.chat.base.ui.avatar.AvatarGenerator
import com.kuymakov.chat.R
import com.kuymakov.chat.base.extensions.lastSeen
import com.kuymakov.chat.base.extensions.load
import com.kuymakov.chat.base.recyclerview.BaseViewHolder
import com.kuymakov.chat.databinding.UserItemBinding
import com.kuymakov.chat.domain.models.User

class UserViewHolder(binding: UserItemBinding) : BaseViewHolder<User>(binding.root) {
    private val usernameView = binding.username
    private val lastSeenView = binding.lastSeen
    private val avatarView = binding.avatar

    override fun onBind(item: User) {
        val context = itemView.context
        with(item) {
            usernameView.text = username
            lastSeenView.text = if (item.status == User.Status.ONLINE) {
                context.resources.getString(R.string.user_status_online)
            } else {
                lastSeen?.lastSeen(context) ?: "Last seen recently"
            }
            avatarView.load(imageUrl) {
                placeholder(AvatarGenerator(itemView.context).generate(username))
            }
        }
    }
}