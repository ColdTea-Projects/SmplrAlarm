package de.coldtea.smplr.smplralarm.extensions

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import de.coldtea.smplr.smplralarm.apis.SmplrAlarmAPI.Companion.SMPLR_ALARM_NOTIFICATION_ID
import de.coldtea.smplr.smplralarm.apis.SmplrAlarmAPI.Companion.SMPLR_ALARM_REQUEST_ID
import de.coldtea.smplr.smplralarm.models.NotificationChannelItem
import de.coldtea.smplr.smplralarm.models.NotificationItem

/**
 * Created by [Yasar Naci Gündüz](https://github.com/ColdTea-Projects).
 */

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

internal fun Context.showNotification(
    requestId: Int,
    notificationChannelItem: NotificationChannelItem,
    notificationItem: NotificationItem,
    fullScreenIntent: Intent? = null
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
            setAllowSystemGeneratedContextualActions(false)

            if(fullScreenIntent != null){
                fullScreenIntent.putExtra(SMPLR_ALARM_REQUEST_ID, requestId)
                val  pendingFullScreenIntent = getFullScreenIntent(fullScreenIntent)

                setFullScreenIntent(pendingFullScreenIntent, true)
            }

            if (notificationItem.firstButtonText != null) addAction(
                0,
                notificationItem.firstButtonText,
                PendingIntent.getBroadcast(
                    this@showNotification,
                    requestId,
                    notificationItem.firstButtonIntent?.apply {
                         putExtra(SMPLR_ALARM_NOTIFICATION_ID, requestId)
                    },
                    0
                )
            )

            if (notificationItem.secondButtonText != null) addAction(
                0,
                notificationItem.secondButtonText,
                PendingIntent.getBroadcast(
                    this@showNotification,
                    requestId,
                    notificationItem.secondButtonIntent?.apply {
                        putExtra(SMPLR_ALARM_NOTIFICATION_ID, requestId)
                    },
                    0
                )
            )
        }
    }.build()

    notificationManager.notify(requestId, notification)

}

internal fun Context.getFullScreenIntent(intent: Intent): PendingIntent =
    PendingIntent.getActivity(this, 0, intent, 0)