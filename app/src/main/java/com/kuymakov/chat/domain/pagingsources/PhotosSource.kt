package com.kuymakov.chat.domain.pagingsources

import com.kuymakov.chat.base.paging.BasePagingSource
import com.kuymakov.chat.domain.models.Photo
import com.kuymakov.chat.domain.repositories.PhotoRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.util.*

class PhotosSource @AssistedInject constructor(
    private val repo: PhotoRepository,
    @Assisted private val pageSize: Int
) : BasePagingSource<Photo>(pageSize) {
    private var maxDate: Date? = null
    override val fetchData: suspend (Int) -> List<Photo> = { size ->
        repo.getPhotos(size, maxDate)
    }

    override fun onNewData(data: List<Photo>) {
        maxDate = data.last().date
    }

    @AssistedFactory
    interface Factory {
        fun create(@Assisted pageSize: Int): PhotosSource
    }
}