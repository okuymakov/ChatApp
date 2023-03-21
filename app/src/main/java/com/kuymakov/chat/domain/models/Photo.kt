package com.kuymakov.chat.domain.models

import android.net.Uri
import com.kuymakov.chat.base.recyclerview.Item
import java.util.*

data class Photo(
    override val id: Long,
    val date: Date,
    val uri: Uri,
) : Item<Long>