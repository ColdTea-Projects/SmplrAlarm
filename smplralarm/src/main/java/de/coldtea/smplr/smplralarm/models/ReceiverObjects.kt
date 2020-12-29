package de.coldtea.smplr.smplralarm.receivers

import android.content.Intent
import androidx.annotation.DrawableRes
import de.coldtea.smplr.smplralarm.models.NotificationChannelItem
import de.coldtea.smplr.smplralarm.models.NotificationItem

internal class SmplrAlarmReceiverObjects {
    companion object {
        const val SMPLR_ALARM_RECEIVER_INTENT_ID = "smplr_alarm_receiver_intent_id"
        var alarmNotification: MutableList<AlarmNotification> = mutableListOf()
    }
}

data class AlarmNotification(
    val alarmNotificationId: Int,
    val notificationChannelItem: NotificationChannelItem,
    val notificationItem: NotificationItem,
    val intent: Intent?,
    val fullScreenIntent: Intent?
)