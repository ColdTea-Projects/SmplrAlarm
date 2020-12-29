package de.coldtea.smplr.smplralarm.managers

import android.app.NotificationManager
import android.content.Intent
import androidx.annotation.DrawableRes
import de.coldtea.smplr.smplralarm.models.NotificationChannelItem

class ChannelManager {

    //region properties

    var importance: Int = NotificationManager.IMPORTANCE_HIGH
    var showBadge: Boolean = false
    var name: String = SMPLR_ALARM_CHANNEL_DEFAULT_NAME
    var description: String = SMPLR_ALARM_CHANNEL_DEFAULT_DESCRIPTION

    //endregion

    //region setters

    fun importance(importance: () -> Int) {
        this.importance = importance()
    }

    fun showBadge(showBadge: () -> Boolean) {
        this.showBadge = showBadge()
    }

    fun name(name: () -> String) {
        this.name = name()
    }

    fun description(description: () -> String) {
        this.description = description()
    }

    //endregion

    //region build

    fun build(): NotificationChannelItem =
        NotificationChannelItem(
            importance,
            showBadge,
            name,
            description
        )

    //endregion

    //region companion

    companion object{
        const val SMPLR_ALARM_CHANNEL_DEFAULT_NAME = "de.coldtea.smplr.alarm.channel"
        const val SMPLR_ALARM_CHANNEL_DEFAULT_DESCRIPTION = "this notification channel is created by SmplrAlarm"
    }

    //endregion
}