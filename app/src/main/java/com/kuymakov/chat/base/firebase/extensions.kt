package com.kuymakov.chat.base.extensions

import android.content.res.Resources.NotFoundException
import com.kuymakov.chat.base.mappers.toObject
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.*

suspend inline fun CollectionReference.lastOrNull(): DocumentSnapshot? {
    return orderBy("date", Query.Direction.DESCENDING)
        .limit(1)
        .get()
        .await()
        .documents
        .firstOrNull()


}

suspend inline fun DocumentReference.getAndAwait(): DocumentSnapshot {
    return get().await()
}

suspend inline fun Query.getAndAwait(): QuerySnapshot {
    return get().await()
}

suspend inline fun CollectionReference.addAndAwait(data: Any): DocumentReference {
    return add(data).await()
}

suspend inline fun DocumentReference.updateAndAwait(field: String, data: Any) {
    update(field, data).await()
}

suspend inline fun Query.firstOrNull(): DocumentSnapshot? {
    return limit(1).get().await().documents.firstOrNull()
}


inline fun <reified T> DocumentSnapshot.toObject(): T {
    val map = data!!.toMutableMap()
    map["id"] = id
    return map.toObject()
}

suspend inline fun <reified T> DocumentSnapshot.getRefAsObject(field: String): T {
    val doc = getDocumentReference(field)?.get()?.await() ?: throw NotFoundException()
    return doc.toObject()
}

inline fun <reified T> DocumentSnapshot.getList(field: String): List<T> {
    val list = get(field) as List<*>
    return list.filterIsInstance<T>()
}

suspend inline fun FirebaseStorage.addImage(bytes: ByteArray): String {
    val imageRef = reference.child("images/${UUID.randomUUID()}.jpg")
    imageRef.putBytes(bytes).await()
    return imageRef.downloadUrl.await().toString()
}
