package de.coldtea.smplr.smplralarm.apis

import android.content.Intent
import androidx.annotation.DrawableRes
import de.coldtea.smplr.smplralarm.models.NotificationItem

/**
 * Created by [Yasar Naci Gündüz](https://github.com/ColdTea-Projects).
 */
class AlarmNotificationAPI {

    //region properties

    @DrawableRes
    internal var smallIcon: Int? = null
    internal var title: String? = null
    internal var message: String? = null
    internal var bigText: String? = null
    internal var autoCancel: Boolean? = null
    internal var firstButtonText: String? = null
    internal var secondButtonText: String? = null
    internal var firstButtonIntent: Intent? = null
    internal var secondButtonIntent: Intent? = null
    internal var notificationDismissedIntent: Intent? = null

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

    fun notificationDismissedIntent(notificationDismissedIntent: () -> Intent) {
        this.notificationDismissedIntent = notificationDismissedIntent()
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
            secondButtonIntent,
            notificationDismissedIntent
        )

    //endregion
}