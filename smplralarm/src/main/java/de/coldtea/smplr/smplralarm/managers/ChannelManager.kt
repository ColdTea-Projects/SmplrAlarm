package de.coldtea.smplr.smplralarm.managers

import android.app.NotificationManager
import android.content.Intent
import androidx.annotation.DrawableRes
import de.coldtea.smplr.smplralarm.models.NotificationChannelItem

class ChannelManager {

    //region properties

    internal var importance: Int = NotificationManager.IMPORTANCE_HIGH
    internal var showBadge: Boolean = false
    internal var name: String = SMPLR_ALARM_CHANNEL_DEFAULT_NAME
    internal var description: String = SMPLR_ALARM_CHANNEL_DEFAULT_DESCRIPTION

    //endregion

    //region setters

    internal fun importance(importance: () -> Int) {
        this.importance = importance()
    }

    internal fun showBadge(showBadge: () -> Boolean) {
        this.showBadge = showBadge()
    }

    internal fun name(name: () -> String) {
        this.name = name()
    }

    internal fun description(description: () -> String) {
        this.description = description()
    }

    //endregion

    //region build

    internal fun build(): NotificationChannelItem =
        NotificationChannelItem(
            importance,
            showBadge,
            name,
            description
        )

    //endregion

    //region companion

    companion object{
        internal const val SMPLR_ALARM_CHANNEL_DEFAULT_NAME = "de.coldtea.smplr.alarm.channel"
        internal const val SMPLR_ALARM_CHANNEL_DEFAULT_DESCRIPTION = "this notification channel is created by SmplrAlarm"
    }

    //endregion
}