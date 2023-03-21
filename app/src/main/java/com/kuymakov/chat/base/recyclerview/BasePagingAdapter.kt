package com.kuymakov.chat.base.recyclerview

import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView

abstract class BasePagingAdapter<T : Item<S>, S, V : BaseViewHolder<T?>>(
    private val onClick: ((T) -> Unit)? = null,
    private val onLongClick: ((T) -> Boolean)? = null
) : PagingDataAdapter<T, V>(diffCallback = BaseDiffUtilItemCallback()) {

    override fun onBindViewHolder(holder: V, position: Int) {
        val item = getItem(position)
        onClick?.run {
            holder.itemView.setOnClickListener {
                if (item != null) {
                    invoke(item)
                }
            }
        }
        onLongClick?.run {
            holder.itemView.setOnLongClickListener {
                if (item != null) {
                    invoke(item)
                } else false
            }
        }
        holder.onBind(getItem(position))
    }
}