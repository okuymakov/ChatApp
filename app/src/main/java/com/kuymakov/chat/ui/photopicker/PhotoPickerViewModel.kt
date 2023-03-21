package com.kuymakov.chat.ui.photopicker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kuymakov.chat.domain.models.Photo
import com.kuymakov.chat.domain.pagingsources.PhotosSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class PhotoPickerViewModel @Inject constructor(
    private val sourceFactory: PhotosSource.Factory,
) : ViewModel() {

    val photos: Flow<PagingData<Photo>> = Pager(
        PagingConfig(
            pageSize = PAGE_SIZE,
            initialLoadSize = PAGE_SIZE * 3,
            enablePlaceholders = true,
            prefetchDistance = PAGE_SIZE / 2,
            jumpThreshold = PAGE_SIZE * 3,
            //maxSize = PAGE_SIZE * 3
        ),
        null
    ) { sourceFactory.create(PAGE_SIZE) }.flow.cachedIn(viewModelScope)

    companion object {
        private const val PAGE_SIZE = 18
    }
}