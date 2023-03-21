package com.kuymakov.chat.ui.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.kuymakov.chat.base.recyclerview.BaseAdapter
import com.kuymakov.chat.databinding.UserItemBinding
import com.kuymakov.chat.domain.models.User

class UsersAdapter(onClick: (User) -> Unit) :
    BaseAdapter<User,String, UserViewHolder>(onClick = onClick) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = UserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }
}