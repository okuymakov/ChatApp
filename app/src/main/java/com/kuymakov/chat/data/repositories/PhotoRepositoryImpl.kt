package com.kuymakov.chat.data.repositories

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import com.kuymakov.chat.domain.models.Photo
import com.kuymakov.chat.domain.repositories.PhotoRepository
import com.kuymakov.chat.di.IoDispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class PhotoRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : PhotoRepository {

    private val collection by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL
            )
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
    }


    override suspend fun getPhotos(
        size: Int,
        maxDate: Date?
    ): List<Photo> = withContext(dispatcher) {
        val photos = mutableListOf<Photo>()
        val columns = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATE_ADDED)
        val selection = MediaStore.Images.Media.DATE_ADDED + "<?"
        val args = arrayOf((maxDate ?: Date()).time.div(1000).toString())
        val sort = MediaStore.Images.Media.DATE_ADDED

        val query = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            val bundle = Bundle().apply {
                putString(ContentResolver.QUERY_ARG_SQL_SELECTION, selection)
                putStringArray(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, args)
                putStringArray(ContentResolver.QUERY_ARG_SORT_COLUMNS, arrayOf(sort))
                putInt(
                    ContentResolver.QUERY_ARG_SORT_DIRECTION,
                    ContentResolver.QUERY_SORT_DIRECTION_DESCENDING
                )
                putInt(ContentResolver.QUERY_ARG_LIMIT, size)
            }
            context.contentResolver.query(collection, columns, bundle, null)
        } else {
            context.contentResolver.query(
                collection, columns,
                selection, args, "$sort DESC LIMIT $size"
            )
        }
        query?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val millis = cursor.getLong(dateColumn) * 1000
                val date = Date(millis)
                val uri =
                    ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                photos.add(Photo(id = id, date = date, uri = uri))
            }
        }
        photos
    }
}