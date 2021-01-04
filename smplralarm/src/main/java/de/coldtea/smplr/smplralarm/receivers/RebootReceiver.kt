package de.coldtea.smplr.smplralarm.receivers

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import de.coldtea.smplr.smplralarm.extensions.showNotification
import de.coldtea.smplr.smplralarm.managers.AlarmNotificationManager
import de.coldtea.smplr.smplralarm.managers.ChannelManager
import de.coldtea.smplr.smplralarm.models.NotificationChannelItem
import de.coldtea.smplr.smplralarm.models.NotificationItem
import de.coldtea.smplr.smplralarm.repository.AlarmNotificationRepository
import timber.log.Timber

internal class RebootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
            try {
                val alarmNotificationRepository = AlarmNotificationRepository(context)
            } catch (e: Exception) {
                Timber.e(e.toString())
            }
    }

    companion object {
        fun build(context: Context): Intent {
            return Intent(context, RebootReceiver::class.java)
        }
    }

}