package de.coldtea.smplr.smplralarm.extensions

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import de.coldtea.smplr.smplralarm.R
import de.coldtea.smplr.smplralarm.models.IntentNotificationItem
import de.coldtea.smplr.smplralarm.models.NotificationChannelItem
import de.coldtea.smplr.smplralarm.models.NotificationItem

private fun Context.initChannelAndReturnName(notificationChannelItem: NotificationChannelItem): String =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channelId = packageName
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        with(notificationChannelItem){
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = description
                setShowBadge(showBadge)
            }
            notificationManager.createNotificationChannel(channel)
        }

        channelId
    }else packageName

fun Context.showNotificationWithIntent(notificationChannelItem: NotificationChannelItem, intentNotificationItem: IntentNotificationItem) {

    val channelId = initChannelAndReturnName(notificationChannelItem)
    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val pendingIntent = getFullScreenIntent(intentNotificationItem.intent)

    val notification = NotificationCompat.Builder(this, channelId).apply {
        priority = NotificationCompat.PRIORITY_HIGH

        setSmallIcon(intentNotificationItem.notificationItem.smallIcon)
        setContentTitle(intentNotificationItem.notificationItem.title)
        setContentText(intentNotificationItem.notificationItem.message)
        setFullScreenIntent(pendingIntent, true)
    }.build()

    notificationManager.notify(0, notification)

}

fun Context.showNotification(notificationChannelItem: NotificationChannelItem, notificationItem: NotificationItem) {
    val channelId = initChannelAndReturnName(notificationChannelItem)
    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val notification =  NotificationCompat.Builder(this, channelId).apply {
        priority = NotificationCompat.PRIORITY_HIGH
        with(notificationItem){
            setSmallIcon(smallIcon)//R.drawable.ic_baseline_child_care_24) // 3
            setContentTitle(title) // 4
            setContentText(message) // 5
            setStyle(NotificationCompat.BigTextStyle().bigText(bigText)) // 6
            priority = NotificationCompat.PRIORITY_DEFAULT // 7
            setAutoCancel(autoCancel) // 8
        }
    }.build()

    notificationManager.notify(0, notification)

}

private fun Context.getFullScreenIntent(intent: Intent): PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)


private const val CHANNEL_ID = "channelId"