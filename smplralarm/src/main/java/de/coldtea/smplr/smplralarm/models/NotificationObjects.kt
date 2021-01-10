package de.coldtea.smplr.smplralarm.models

import android.content.Intent
import androidx.annotation.DrawableRes

data class NotificationChannelItem(
    val importance: Int,
    val showBadge: Boolean,
    val name: String,
    val description: String
)

data class NotificationItem(
    @DrawableRes
    val smallIcon: Int,
    val title: String,
    val message: String,
    val bigText: String,
    val autoCancel: Boolean
)

data class IntentNotificationItem(
    val intent: Intent?,
    val notificationItem: NotificationItem
)