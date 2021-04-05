package de.coldtea.smplr.smplralarm.managers

import android.content.Intent
import androidx.annotation.DrawableRes
import de.coldtea.smplr.smplralarm.models.NotificationItem

class AlarmNotificationManager {

    //region properties

    @DrawableRes
    internal var smallIcon: Int = android.R.drawable.ic_lock_idle_alarm
    internal var title: String = SMPLR_ALARM_CHANNEL_DEFAULT_TITLE
    internal var message: String = SMPLR_ALARM_CHANNEL_DEFAULT_MESSAGE
    internal var bigText: String = SMPLR_ALARM_CHANNEL_DEFAULT_BIG_TEXT
    internal var autoCancel: Boolean = true
    internal var alarmIntent: Intent? = null

    //endregion

    //region setters

    internal fun smallIcon(smallIcon: () -> Int) {
        this.smallIcon = smallIcon()
    }

    internal fun title(title: () -> String) {
        this.title = title()
    }

    internal fun message(message: () -> String) {
        this.message = message()
    }

    internal fun bigText(bigText: () -> String) {
        this.bigText = bigText()
    }

    internal fun autoCancel(autoCancel: () -> Boolean) {
        this.autoCancel = autoCancel()
    }

    internal fun alarmIntent(alarmIntent: () -> Intent) {
        this.alarmIntent = alarmIntent()
    }

    //endregion

    //region build

    internal fun build(): NotificationItem =
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
        internal const val SMPLR_ALARM_CHANNEL_DEFAULT_TITLE = "de.coldtea.smplr.alarm.channel"
        internal const val SMPLR_ALARM_CHANNEL_DEFAULT_MESSAGE = "Smplr Alarm"
        internal const val SMPLR_ALARM_CHANNEL_DEFAULT_BIG_TEXT = "Smplr Alarm is ringing!"
    }

    //endregion
}