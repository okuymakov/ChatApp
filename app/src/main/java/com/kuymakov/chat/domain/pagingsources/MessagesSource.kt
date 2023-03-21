package com.kuymakov.chat.domain.pagingsources

import com.kuymakov.chat.base.paging.BasePagingSource
import com.kuymakov.chat.base.network.Success
import com.kuymakov.chat.domain.models.Message
import com.kuymakov.chat.domain.repositories.MessageRepository
import com.kuymakov.chat.domain.models.MessageItem
import com.kuymakov.chat.domain.models.MessagesGroupDate
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class MessagesSource @AssistedInject constructor(
    private val repo: MessageRepository,
    @Assisted private val chatId: String,
    @Assisted private val pageSize: Int
) : BasePagingSource<MessageItem>(pageSize) {
    private var maxDate: LocalDateTime? = null

    override val fetchData: suspend (Int) -> List<MessageItem> = { size ->
        val res = repo.getMessages(chatId, size, maxDate)
        if (res is Success) {
            val messageItems = mutableListOf<MessageItem>()
            val allMessages = res.data
            maxDate?.toLocalDate()?.let {
                if (allMessages.isEmpty() || allMessages.first().date.toLocalDate() > it) {
                    messageItems.add(MessagesGroupDate(date = it))
                }
            }
            allMessages.groupBy { msg -> msg.date.toLocalDate() }.forEach { (date, messages) ->
                messageItems.addAll(messages)
                if (allMessages.last().date.toLocalDate() != date || allMessages.size < pageSize) {
                    messageItems.add(MessagesGroupDate(date = date))
                }
            }
            messageItems
        } else error("")
    }

    override fun onNewData(data: List<MessageItem>) {
        maxDate = data.filterIsInstance(Message::class.java).last().date
    }

    @AssistedFactory
    interface Factory {
        fun create(@Assisted chatId: String, @Assisted pageSize: Int): MessagesSource
    }
}