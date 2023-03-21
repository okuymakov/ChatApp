package com.kuymakov.chat.domain.usecases

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.kuymakov.chat.domain.models.MessageItem
import com.kuymakov.chat.domain.pagingsources.MessagesSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class GetMessagesUseCase @Inject constructor(
    private val sourceFactory: MessagesSource.Factory,
) {
    operator fun invoke(chatId: String): Flow<PagingData<MessageItem>> {
        return Pager(
            PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE * 2,
            ),
            null
        ) {
            sourceFactory.create(chatId, PAGE_SIZE)
        }.flow
    }


    companion object {
        private const val PAGE_SIZE = 40
    }
}