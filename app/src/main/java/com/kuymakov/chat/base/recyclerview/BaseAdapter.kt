package com.kuymakov.chat.base.recyclerview

import androidx.recyclerview.widget.ListAdapter

abstract class BaseAdapter<T : Item<S>, S, V : BaseViewHolder<T>>(
    private val onClick: ((T) -> Unit)? = null,
    private val onLongClick: ((T) -> Boolean)? = null
) : ListAdapter<T, V>(BaseDiffUtilItemCallback<T, S>()) {

    override fun onBindViewHolder(holder: V, position: Int) {
        val item = getItem(position)
        onClick?.run {
            holder.itemView.setOnClickListener {
                invoke(item)
            }
        }
        onLongClick?.run {
            holder.itemView.setOnLongClickListener {
                invoke(item)
            }
        }
        holder.onBind(getItem(position))
    }
}