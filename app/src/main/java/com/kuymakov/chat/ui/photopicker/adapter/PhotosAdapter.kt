package com.kuymakov.chat.ui.photopicker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.kuymakov.chat.base.recyclerview.BasePagingAdapter
import com.kuymakov.chat.databinding.PhotoItemBinding
import com.kuymakov.chat.domain.models.Photo

class PhotosAdapter(onClick: (Photo) -> Unit) :
    BasePagingAdapter<Photo, Long, PhotoViewHolder>(onClick = onClick) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = PhotoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PhotoViewHolder(binding)
    }

}