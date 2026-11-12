package com.aayan.deletedmsgrecovery

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A message snapshot taken the moment WhatsApp posted a notification
 * for it — captured BEFORE the sender has any chance to delete it.
 *
 * Only messages addressed to this device are ever visible here;
 * there is no access to other users' chats, servers, or backups.
 */
@Entity(tableName = "cached_messages")
data class CachedMessage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sender: String,          // Notification title (contact/group name)
    val content: String,         // Notification text at time of posting
    val timestampMillis: Long,   // When we captured it
    val wasMarkedDeleted: Boolean = false // Flipped true once WA shows "message was deleted"
)
