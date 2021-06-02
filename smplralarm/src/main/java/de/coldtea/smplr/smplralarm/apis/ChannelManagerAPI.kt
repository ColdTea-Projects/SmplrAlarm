package de.coldtea.smplr.smplralarm.apis

import android.app.NotificationManager
import de.coldtea.smplr.smplralarm.models.NotificationChannelItem

/**
 * Created by [Yasar Naci Gündüz](https://github.com/ColdTea-Projects).
 */
class ChannelManagerAPI {

    //region properties

    internal var importance: Int = NotificationManager.IMPORTANCE_HIGH
    internal var showBadge: Boolean = false
    internal var name: String = SMPLR_ALARM_CHANNEL_DEFAULT_NAME
    internal var description: String = SMPLR_ALARM_CHANNEL_DEFAULT_DESCRIPTION

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
        internal const val SMPLR_ALARM_CHANNEL_DEFAULT_NAME = "de.coldtea.smplr.alarm.channel"
        internal const val SMPLR_ALARM_CHANNEL_DEFAULT_DESCRIPTION = "this notification channel is created by SmplrAlarm"
    }

    //endregion
}