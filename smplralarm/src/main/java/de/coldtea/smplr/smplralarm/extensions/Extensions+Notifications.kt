package de.coldtea.smplr.smplralarm.extensions

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import de.coldtea.smplr.smplralarm.apis.SmplrAlarmAPI.Companion.SMPLR_ALARM_NOTIFICATION_ID
import de.coldtea.smplr.smplralarm.models.IntentNotificationItem
import de.coldtea.smplr.smplralarm.models.NotificationChannelItem
import de.coldtea.smplr.smplralarm.models.NotificationItem
import de.coldtea.smplr.smplralarm.receivers.AlarmReceiver

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
    requestId: Int,
    notificationChannelItem: NotificationChannelItem,
    intentNotificationItem: IntentNotificationItem
) {
    val pendingIntent = getFullScreenIntent(intentNotificationItem.intent)

    this.showNotification(
        requestId,
        notificationChannelItem,
        intentNotificationItem.notificationItem,
        pendingIntent
    )
}

internal fun Context.showNotification(
    requestId: Int,
    notificationChannelItem: NotificationChannelItem,
    notificationItem: NotificationItem,
    pendingIntent: PendingIntent? = null
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

            if(pendingIntent != null) setFullScreenIntent(pendingIntent, true)

            setContentIntent(getContentIntent(requestId = requestId))

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

    notificationManager.notify(requestId, notification)

}

internal fun Context.getFullScreenIntent(intent: Intent?): PendingIntent =
    PendingIntent.getActivity(this, 0, intent, 0)

private fun Context.getContentIntent(requestId: Int): PendingIntent{
    val contentIntent = Intent(this, AlarmReceiver::class.java)
    contentIntent.putExtra(SMPLR_ALARM_NOTIFICATION_ID, requestId)

    return PendingIntent.getBroadcast(
        this,
        0,
        contentIntent,
        0
    )
}