package com.aayan.deletedmsgrecovery

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val WHATSAPP_PACKAGE = "com.whatsapp"
private const val WHATSAPP_BUSINESS_PACKAGE = "com.whatsapp.w4b"

/**
 * Captures WhatsApp notifications the instant they're posted — before
 * the sender has any opportunity to delete the message. If WhatsApp
 * later re-posts the same conversation's notification showing
 * "This message was deleted", we mark our cached copy accordingly
 * instead of losing it.
 *
 * Scope: only notifications for messages sent TO this device are ever
 * seen. There is no interception of WhatsApp's network traffic, no
 * access to other people's devices or chats, and no handling of
 * "view once" media — that's intentionally out of scope.
 *
 * Requires the user to manually enable this app under
 * Settings > Apps > Special access > Notification access.
 */
class MessageListenerService : NotificationListenerService() {

    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (sbn.packageName != WHATSAPP_PACKAGE && sbn.packageName != WHATSAPP_BUSINESS_PACKAGE) {
            return
        }

        val extras = sbn.notification.extras
        val sender = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: return
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: return

        // Ignore WhatsApp's own summary/group notifications ("3 new messages")
        if (extras.getBoolean(Notification.EXTRA_SHOW_CHRONOMETER, false)) return

        val dao = AppDatabase.getInstance(applicationContext).messageDao()

        scope.launch {
            val recent = dao.recentFromSender(sender)

            val deletedPhraseMatch = recent.firstOrNull { cached ->
                !cached.wasMarkedDeleted &&
                    (text.contains("deleted this message", ignoreCase = true) ||
                     text.contains("This message was deleted", ignoreCase = true))
            }

            if (deletedPhraseMatch != null) {
                // WhatsApp is telling us the previous message got deleted —
                // flag our already-cached copy rather than storing this
                // placeholder text as if it were new content.
                dao.update(deletedPhraseMatch.copy(wasMarkedDeleted = true))
            } else {
                dao.insert(
                    CachedMessage(
                        sender = sender,
                        content = text,
                        timestampMillis = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    // Deliberately no override of onNotificationRemoved beyond the default:
    // removal just means the notification left the shade, not that the
    // message is gone — our cached copy in Room is unaffected either way.
}
