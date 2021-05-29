package de.coldtea.smplr.smplralarm.extensions

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import de.coldtea.smplr.smplralarm.models.IntentNotificationItem
import de.coldtea.smplr.smplralarm.models.NotificationChannelItem
import de.coldtea.smplr.smplralarm.models.NotificationItem

private fun Context.initChannelAndReturnName(notificationChannelItem: NotificationChannelItem): String =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channelId = packageName
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        with(notificationChannelItem) {
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = description
                setShowBadge(showBadge)
            }
            notificationManager.createNotificationChannel(channel)
        }

        channelId
    } else packageName

internal fun Context.showNotificationWithIntent(
    notificationChannelItem: NotificationChannelItem,
    intentNotificationItem: IntentNotificationItem
) {

    val channelId = initChannelAndReturnName(notificationChannelItem)
    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val pendingIntent = getFullScreenIntent(intentNotificationItem.intent)

    val notification = NotificationCompat.Builder(this, channelId).apply {
        priority = NotificationCompat.PRIORITY_HIGH

        setSmallIcon(intentNotificationItem.notificationItem.smallIcon)
        setContentTitle(intentNotificationItem.notificationItem.title)
        setContentText(intentNotificationItem.notificationItem.message)
        setFullScreenIntent(pendingIntent, true)

        val notificationItem = intentNotificationItem.notificationItem

        if (notificationItem.firstButtonText != null) addAction(
            0,
            notificationItem.firstButtonText,
            PendingIntent.getBroadcast(
                this@showNotificationWithIntent,
                0,
                notificationItem.firstButtonIntent,
                0
            )
        )

        if (notificationItem.secondButtonText != null) addAction(
            0,
            notificationItem.secondButtonText,
            PendingIntent.getBroadcast(
                this@showNotificationWithIntent,
                0,
                notificationItem.secondButtonIntent,
                0
            )
        )
    }.build()

    notificationManager.notify(0, notification)

}

internal fun Context.showNotification(
    notificationChannelItem: NotificationChannelItem,
    notificationItem: NotificationItem
) {
    val channelId = initChannelAndReturnName(notificationChannelItem)
    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val notification = NotificationCompat.Builder(this, channelId).apply {
        priority = NotificationCompat.PRIORITY_HIGH
        with(notificationItem) {
            setSmallIcon(smallIcon)
            setContentTitle(title)
            setContentText(message)
            setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            priority = NotificationCompat.PRIORITY_DEFAULT
            setAutoCancel(autoCancel)

            if (notificationItem.firstButtonText != null) addAction(
                0,
                notificationItem.firstButtonText,
                PendingIntent.getBroadcast(
                    this@showNotification,
                    0,
                    notificationItem.firstButtonIntent,
                    0
                )
            )

            if (notificationItem.secondButtonText != null) addAction(
                0,
                notificationItem.secondButtonText,
                PendingIntent.getBroadcast(
                    this@showNotification,
                    0,
                    notificationItem.secondButtonIntent,
                    0
                )
            )
        }
    }.build()

    notificationManager.notify(0, notification)

}

internal fun Context.getFullScreenIntent(intent: Intent?): PendingIntent =
    PendingIntent.getActivity(this, 0, intent, 0)