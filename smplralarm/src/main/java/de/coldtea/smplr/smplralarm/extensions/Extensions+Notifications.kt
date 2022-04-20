package de.coldtea.smplr.smplralarm.extensions

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import de.coldtea.smplr.smplralarm.R
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
                setSound(null ,null)
            }
            notificationManager.createNotificationChannel(channel)
        }

        channelId
    } else packageName

internal fun Context.showNotification(
    requestId: Int,
    notificationChannelItem: NotificationChannelItem,
    notificationItem: NotificationItem,
    contentIntent: Intent? = null,
    alarmReceivedIntent: Intent? = null,
    fullScreenIntent: Intent? = null
) {
    val channelId = initChannelAndReturnName(notificationChannelItem)
    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val notification = NotificationCompat.Builder(this, channelId).apply {
        priority = NotificationCompat.PRIORITY_HIGH
        with(notificationItem) {
            setSmallIcon(smallIcon?: R.drawable.ic_baseline_notifications_active_24)
            setContentTitle(title)
            setContentText(message)
            setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            priority = NotificationCompat.PRIORITY_DEFAULT
            setAutoCancel(autoCancel?:true)
            setAllowSystemGeneratedContextualActions(false)

            if (notificationItem.notificationDismissedIntent != null){
                setDeleteIntent(
                    this@showNotification.getBroadcast(
                        requestId,
                        requireNotNull(notificationItem.notificationDismissedIntent)
                    )
                )
            }

            if (contentIntent != null) {
                setContentIntent(
                    getScreenIntent(requestId, contentIntent)
                )
            }

            if (fullScreenIntent != null) {
                setFullScreenIntent(
                    getScreenIntent(requestId, fullScreenIntent),
                    true
                )
            }

            if (notificationItem.firstButtonText != null && notificationItem.firstButtonIntent != null) addAction(
                0,
                notificationItem.firstButtonText,
                this@showNotification.getBroadcast(
                    requestId,
                    requireNotNull(notificationItem.firstButtonIntent)
                )
            )

            if (notificationItem.secondButtonText != null && notificationItem.secondButtonIntent != null) addAction(
                0,
                notificationItem.secondButtonText,
                this@showNotification.getBroadcast(
                    requestId,
                    requireNotNull(notificationItem.secondButtonIntent)
                )
            )
        }
    }.build()

    notificationManager.notify(requestId, notification)

    if (alarmReceivedIntent != null) {
        alarmReceivedIntent.putExtra(SMPLR_ALARM_REQUEST_ID, requestId)
        sendBroadcast(alarmReceivedIntent)
    }
}

internal fun Context.getScreenIntent(requestId: Int, intent: Intent): PendingIntent =
    PendingIntent.getActivity(
        this,
        requestId,
        intent,
        PendingIntent.FLAG_IMMUTABLE
    )
private fun Context.getBroadcast(requestId: Int, intent: Intent): PendingIntent =
    PendingIntent.getBroadcast(
        this,
        requestId,
        intent.apply {
            putExtra(SMPLR_ALARM_NOTIFICATION_ID, requestId)
        },
        PendingIntent.FLAG_IMMUTABLE
    )