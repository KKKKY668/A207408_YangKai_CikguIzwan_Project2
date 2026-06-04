package com.example.a207408_cikguizwan_lab02

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

data class CommunityLog(
    val id: String = "",
    val species: String = "",
    val location: String = "",
    val sharedBy: String = "",
    val time: String = ""
)

object FirestoreService {

    private val db = Firebase.firestore
    private val collection = db.collection("community_logs")

    // 上传一条记录到 Firestore
    suspend fun shareLog(log: ActivityLog, userName: String) {
        val data = hashMapOf(
            "species" to log.species,
            "location" to log.location,
            "sharedBy" to userName.ifBlank { "Anonymous" },
            "time" to log.time
        )
        collection.add(data).await()
    }

    // 实时监听 Firestore 数据变化，返回 Flow
    fun getCommunityLogs(): Flow<List<CommunityLog>> = callbackFlow {
        val listener = collection
            .orderBy("time")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val logs = snapshot.documents.map { doc ->
                    CommunityLog(
                        id = doc.id,
                        species = doc.getString("species") ?: "",
                        location = doc.getString("location") ?: "",
                        sharedBy = doc.getString("sharedBy") ?: "Anonymous",
                        time = doc.getString("time") ?: ""
                    )
                }
                trySend(logs)
            }
        awaitClose { listener.remove() }
    }
}