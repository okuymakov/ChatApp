package com.kuymakov.chat.base.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import java.io.IOException

abstract class BasePagingSource<T : Any>(private val pageSize: Int) : PagingSource<Int, T>() {
    protected abstract val fetchData: suspend (Int) -> List<T>

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        return try {
            val page = params.key ?: 1
            val loadSize = params.loadSize
            val data = fetchData(loadSize)
            onNewData(data)
            LoadResult.Page(
                data = data,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (data.size < loadSize) null else page + loadSize / pageSize,
            )
        } catch (e: IOException) {
            return LoadResult.Error(e)
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    protected abstract fun onNewData(data: List<T>)

    override val jumpingSupported = true

    override fun getRefreshKey(state: PagingState<Int, T>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchorPosition) ?: return null
        return page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
    }
}