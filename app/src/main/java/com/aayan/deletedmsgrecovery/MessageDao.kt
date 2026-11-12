package com.aayan.deletedmsgrecovery

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MessageDao {

    @Insert
    suspend fun insert(message: CachedMessage): Long

    @Query("SELECT * FROM cached_messages ORDER BY timestampMillis DESC")
    fun getAll(): LiveData<List<CachedMessage>>

    // Used to find the most recent message from this sender with this
    // exact text, so we can flag it as deleted rather than inserting
    // a duplicate row when WhatsApp re-posts the "deleted" notification.
    @Query(
        "SELECT * FROM cached_messages WHERE sender = :sender " +
        "ORDER BY timestampMillis DESC LIMIT 5"
    )
    suspend fun recentFromSender(sender: String): List<CachedMessage>

    @Update
    suspend fun update(message: CachedMessage)

    @Query("DELETE FROM cached_messages")
    suspend fun clearAll()
}
