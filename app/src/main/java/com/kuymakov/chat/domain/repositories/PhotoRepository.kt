package com.kuymakov.chat.domain.repositories

import com.kuymakov.chat.domain.models.Photo
import java.util.Date

interface PhotoRepository {
    suspend fun getPhotos(size: Int, maxDate: Date? = null): List<Photo>
}