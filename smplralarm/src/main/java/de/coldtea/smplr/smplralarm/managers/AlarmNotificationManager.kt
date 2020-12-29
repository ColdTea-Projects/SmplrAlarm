package de.coldtea.smplr.smplralarm.managers

import android.content.Intent
import androidx.annotation.DrawableRes
import de.coldtea.smplr.smplralarm.models.NotificationItem

class AlarmNotificationManager {

    //region properties

    @DrawableRes
    var smallIcon: Int = android.R.drawable.ic_lock_idle_alarm
    var title: String = SMPLR_ALARM_CHANNEL_DEFAULT_TITLE
    var message: String = SMPLR_ALARM_CHANNEL_DEFAULT_MESSAGE
    var bigText: String = SMPLR_ALARM_CHANNEL_DEFAULT_BIG_TEXT
    var autoCancel: Boolean = true
    var alarmIntent: Intent? = null

    //endregion

    //region setters

    fun smallIcon(smallIcon: () -> Int) {
        this.smallIcon = smallIcon()
    }

    fun title(title: () -> String) {
        this.title = title()
    }

    fun message(message: () -> String) {
        this.message = message()
    }

    fun bigText(bigText: () -> String) {
        this.bigText = bigText()
    }

    fun autoCancel(autoCancel: () -> Boolean) {
        this.autoCancel = autoCancel()
    }

    fun alarmIntent(alarmIntent: () -> Intent) {
        this.alarmIntent = alarmIntent()
    }

    //endregion

    //region build

    fun build(): NotificationItem =
        NotificationItem(
            smallIcon,
            title,
            message,
            bigText,
            autoCancel
        )

    //endregion

    //region companion

    companion object{
        const val SMPLR_ALARM_CHANNEL_DEFAULT_TITLE = "de.coldtea.smplr.alarm.channel"
        const val SMPLR_ALARM_CHANNEL_DEFAULT_MESSAGE = "Smplr Alarm"
        const val SMPLR_ALARM_CHANNEL_DEFAULT_BIG_TEXT = "Smplr Alarm is ringing!"
    }

    //endregion
}