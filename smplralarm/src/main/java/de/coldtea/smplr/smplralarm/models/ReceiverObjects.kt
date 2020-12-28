package de.coldtea.smplr.smplralarm.receivers

import android.content.Intent
import androidx.core.app.NotificationCompat

internal class SmplrAlarmReceiverObjects {
    companion object{
        const val SMPLR_ALARM_RECEIVER_INTENT_ID = "smplr_alarm_receiver_intent_id"
        var notificationBuilders: MutableList<NotificationBuilderItem> = mutableListOf()
    }
}

data class NotificationBuilderItem(
        val notificationBuilderId: Int,
        val notificationBuilder: NotificationCompat.Builder,
        val intent: Intent
        )