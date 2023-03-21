package com.kuymakov.chat.data

import com.kuymakov.chat.domain.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.FirebaseFirestore
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class UserPresenceHandler @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseDatabase,
    private val firestore: FirebaseFirestore
) {
    fun init() {
        val uid = auth.currentUser?.uid ?: return

        val statusRef = db.getReference("$uid/status")
        val lastSeenRef = db.getReference("$uid/lastSeen")
        db.getReference(".info/connected").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue<Boolean>() ?: false
                if (connected) {
                    statusRef.apply {
                        setValue(User.Status.ONLINE)
                        onDisconnect().setValue(User.Status.OFFLINE)
                    }
                    lastSeenRef.onDisconnect().setValue(Date())
                    firestore.document("users/$uid").update("status", User.Status.ONLINE)
                } else {
                    firestore.document("users/$uid").update(
                        "status", User.Status.OFFLINE,
                        "lastSeen", Date()
                    )
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.w("Listener was cancelled at .info/connected")
            }
        })
    }

    companion object {
        val currentUser get() = FirebaseAuth.getInstance().currentUser
    }
}