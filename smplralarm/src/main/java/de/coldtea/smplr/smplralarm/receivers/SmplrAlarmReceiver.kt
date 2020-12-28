package de.coldtea.smplr.smplralarm.receivers

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import de.coldtea.smplr.smplralarm.extensions.showNotificationWithIntent
import de.coldtea.smplr.smplralarm.models.IntentNotificationItem
import de.coldtea.smplr.smplralarm.models.NotificationChannelItem
import de.coldtea.smplr.smplralarm.receivers.SmplrAlarmReceiverObjects.Companion.SMPLR_ALARM_RECEIVER_INTENT_ID
import de.coldtea.smplr.smplralarm.receivers.SmplrAlarmReceiverObjects.Companion.notificationBuilders
import timber.log.Timber

internal class SmplrAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
            try {
                notificationBuilders.find {
                    it.notificationBuilderId == intent.getIntExtra(SMPLR_ALARM_RECEIVER_INTENT_ID, 0)
                }?.let {

//                    val notificationManager = NotificationManagerCompat.from(context)
//
//                    notificationManager.notify( it.notificationBuilderId,
//                            it.notificationBuilder.build())

                    context.showNotificationWithIntent(
                        NotificationChannelItem(
                            NotificationManager.IMPORTANCE_HIGH,
                            false,
                            "dummy channel",
                            "I am a dummy channel"
                        ),
                        IntentNotificationItem(
                            intent,
                            android.R.drawable.arrow_up_float,
                            "SmplrDummy",
                            "Smplr Dummy message",
                            "OOO yegenim nasilsin yav",
                            true
                        )
                    )

                }
            } catch (e: Exception) {
                Timber.e(e.toString())
            }
    }

    companion object {
        fun build(context: Context): Intent {
            return Intent(context, SmplrAlarmReceiver::class.java)
        }
    }

}