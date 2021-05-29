package de.coldtea.smplr.smplralarm.apis

import android.content.Intent
import androidx.annotation.DrawableRes
import de.coldtea.smplr.smplralarm.models.NotificationItem

class AlarmNotificationAPI {

    //region properties

    @DrawableRes
    internal var smallIcon: Int = android.R.drawable.ic_lock_idle_alarm
    internal var title: String = SMPLR_ALARM_DEFAULT_TITLE
    internal var message: String = SMPLR_ALARM_DEFAULT_MESSAGE
    internal var bigText: String = SMPLR_ALARM_DEFAULT_BIG_TEXT
    internal var autoCancel: Boolean = true
    internal var alarmIntent: Intent? = null
    internal var firstButtonText: String? = null
    internal var secondButtonText: String? = null
    internal var firstButtonIntent: Intent? = null
    internal var secondButtonIntent: Intent? = null

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


    fun firstButtonText(firstButtonText: () -> String) {
        this.firstButtonText = firstButtonText()
    }

    fun secondButtonText(secondButtonText: () -> String) {
        this.secondButtonText = secondButtonText()
    }

    fun firstButtonIntent(firstButtonIntent: () -> Intent) {
        this.firstButtonIntent = firstButtonIntent()
    }

    fun secondButtonIntent(secondButtonIntent: () -> Intent) {
        this.secondButtonIntent = secondButtonIntent()
    }

    //endregion

    //region build

    internal fun build(): NotificationItem =
        NotificationItem(
            smallIcon,
            title,
            message,
            bigText,
            autoCancel,
            firstButtonText,
            secondButtonText,
            firstButtonIntent,
            secondButtonIntent
        )

    //endregion

    //region companion

    companion object{
        internal const val SMPLR_ALARM_DEFAULT_TITLE = "de.coldtea.smplr.alarm.channel"
        internal const val SMPLR_ALARM_DEFAULT_MESSAGE = "Smplr Alarm"
        internal const val SMPLR_ALARM_DEFAULT_BIG_TEXT = "Smplr Alarm is ringing!"
    }

    //endregion
}