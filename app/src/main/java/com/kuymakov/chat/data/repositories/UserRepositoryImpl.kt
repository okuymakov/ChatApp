package com.kuymakov.chat.data.repositories

import android.content.res.Resources.NotFoundException
import com.kuymakov.chat.base.extensions.*
import com.kuymakov.chat.base.mappers.toMap
import com.kuymakov.chat.base.network.Response
import com.kuymakov.chat.domain.models.User
import com.kuymakov.chat.domain.network.request.UserRequest
import com.kuymakov.chat.domain.repositories.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.kuymakov.chat.base.firebase.exceptionsWrapper
import com.kuymakov.chat.base.network.Success
import com.kuymakov.chat.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    db: FirebaseFirestore,
    private val storage: FirebaseStorage,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : UserRepository {
    private val usersCollection = db.collection("users")

    override suspend fun fetchUsers(username: String): Response<List<User>> =
        withContext(dispatcher) {
            exceptionsWrapper {
                val users = usersCollection
                    .whereGreaterThanOrEqualTo("username", username)
                    .whereLessThan("username", username.dropLast(1) + (username.last() + 1))
                    .getAndAwait()
                    .map { it.toObject<User>() }
                Success(users)
            }
        }

    override suspend fun getUser(userId: String): Response<User> = withContext(dispatcher) {
        exceptionsWrapper {
            val userDoc = usersCollection.document(userId)
                .getAndAwait()
            val user = userDoc.toObject<User>()
            Success(user)
        }
    }

    override suspend fun getUserByEmail(email: String): Response<User> = withContext(dispatcher) {
        exceptionsWrapper {
            val userDoc = usersCollection.whereEqualTo("email", email).firstOrNull()
                ?: throw NotFoundException()
            val user = userDoc.toObject<User>()
            Success(user)
        }
    }

    override suspend fun createUser(user: UserRequest): Response<Unit> = withContext(dispatcher) {
        exceptionsWrapper {
            val userMap = user.toMap().toMutableMap()
            val image = user.image
            if (image != null) {
                val imageUrl = storage.addImage(image)
                userMap["imageUrl"] = imageUrl
            }
            usersCollection.document(user.id!!).set(userMap).await()
            Success(Unit)
        }
    }

    override suspend fun updateUser(user: UserRequest): Response<Unit> = withContext(dispatcher) {
        exceptionsWrapper {
            val userId = user.id!!
            val userMap = user.toMap().toMutableMap()
            val image = user.image
            if (image != null) {
                val imageUrl = storage.addImage(image)
                userMap["imageUrl"] = imageUrl
            }
            usersCollection.document(userId).update(userMap).await()
            Success(Unit)
        }
    }

    override suspend fun deleteUser(userId: String): Response<Unit> = withContext(dispatcher) {
        exceptionsWrapper {
            usersCollection.document(userId).delete().await()
            Success(Unit)
        }
    }

}