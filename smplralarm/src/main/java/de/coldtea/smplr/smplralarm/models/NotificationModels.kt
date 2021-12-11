package de.coldtea.smplr.smplralarm.models

import android.content.Intent
import androidx.annotation.DrawableRes

/**
 * Created by [Yasar Naci Gündüz](https://github.com/ColdTea-Projects).
 */
data class NotificationChannelItem(
    val importance: Int,
    val showBadge: Boolean,
    val name: String,
    val description: String
)

data class NotificationItem(
    @DrawableRes
    val smallIcon: Int? = null,
    val title: String? = null,
    val message: String? = null,
    val bigText: String? = null,
    val autoCancel: Boolean? = null,
    val firstButtonText: String? = null,
    val secondButtonText: String? = null,
    var firstButtonIntent: Intent? = null,
    var secondButtonIntent: Intent? = null,
    var notificationDismissedIntent: Intent? = null
)

internal data class IntentNotificationItem(
    val intent: Intent?,
    val notificationItem: NotificationItem
)