package com.kuymakov.chat.ui.chat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kuymakov.chat.base.recyclerview.BaseDiffUtilItemCallback
import com.kuymakov.chat.databinding.MessageDateBinding
import com.kuymakov.chat.databinding.MessageItemBinding
import com.kuymakov.chat.domain.models.Message
import com.kuymakov.chat.domain.models.MessageItem
import com.kuymakov.chat.domain.models.MessagesGroupDate

class MessagesAdapter(
    private val onClick: (Message) -> Unit,
    private val onLongClick: (Message) -> Unit
) : ListAdapter<MessageItem, RecyclerView.ViewHolder>(BaseDiffUtilItemCallback<MessageItem,String>()) {


    val selected: List<String> get() {
        return currentList.filterIsInstance(Message::class.java).filter { it.isSelected }.map { it.id }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is MessageDateViewHolder -> {
                holder.onBind(item as? MessagesGroupDate)
            }
            is MessageViewHolder -> {
                holder.itemView.setOnClickListener {
                    if (item != null) {
                        onClick(item as Message)
                    }
                }
                holder.itemView.setOnLongClickListener {
                    if (item != null) {
                        onLongClick(item as Message)
                        true
                    } else false
                }
                holder.onBind(item as? Message)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is MessagesGroupDate -> DATE
            else -> MESSAGE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            DATE -> {
                val binding = MessageDateBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                MessageDateViewHolder(binding)
            }
            MESSAGE -> {
                val binding = MessageItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                MessageViewHolder(binding)
            }
            else -> error("Unreachable")
        }
    }

    companion object ViewType {
        private const val DATE = 0
        private const val MESSAGE = 1
    }
}