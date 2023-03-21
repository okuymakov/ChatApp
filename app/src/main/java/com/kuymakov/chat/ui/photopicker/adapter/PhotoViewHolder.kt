package com.kuymakov.chat.ui.photopicker.adapter

import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.kuymakov.chat.R
import com.kuymakov.chat.base.recyclerview.BaseViewHolder
import com.kuymakov.chat.domain.models.Photo
import com.kuymakov.chat.databinding.PhotoItemBinding

class PhotoViewHolder(binding: PhotoItemBinding) : BaseViewHolder<Photo?>(binding.root) {
    private val photoView = binding.photo
    override fun onBind(item: Photo?) {
        val placeholder = ContextCompat.getDrawable(
            itemView.context,
            R.drawable.img_placeholder
        )
        Glide.with(photoView.context)
            .load(item?.uri)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .placeholder(placeholder)
            .error(placeholder)
            .into(photoView)

    }
}