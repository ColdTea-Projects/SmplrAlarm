package de.coldtea.smplr.smplralarm

import android.content.Context
import de.coldtea.smplr.smplralarm.apis.AlarmNotificationAPI
import de.coldtea.smplr.smplralarm.apis.ChannelManagerAPI
import de.coldtea.smplr.smplralarm.apis.SmplrAlarmAPI
import de.coldtea.smplr.smplralarm.apis.SmplrAlarmListRequestAPI
import de.coldtea.smplr.smplralarm.models.NotificationChannelItem
import de.coldtea.smplr.smplralarm.models.NotificationItem

/**
 * SmplrAlarm Library, Created by [Yasar Naci Gündüz](https://github.com/ColdTea-Projects).
 *
 * SmplrAlarm is a convenience library to create alarms way simpler than default way.
 * Main goal of this library is providing a clean, simple and convenient API to manage alarms.
 *
 * MIT License
 * Copyright (c) 2020 Yasar Naci Gündüz
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */


/**
 * API interface for setting an alarm.
 * This function requires proper setup to execute different alarm notifications.
 * It accepts both obligatory and optional parameters which explained below, and
 * based on the combination of which, this function is able to set an alarm which
 * serves different purposes. This call returns a request code which serves as an unique
 * identifier for the alarm set. This id can be used later for cancellation and updates
 *
 * Arguments:
 *
 * Constructor arguments :
 * - context : It is allowed to send any kind of context, but it is preferable to send application context to make sure alarm works when the application is not active
 *
 * Obligatory arguments:
 * - hour : Hour of the day in 24h format.
 * - min : Minute of the hour.
 *
 * Optional arguments which will be substituted with dummy items
 * - alarmNotification: A data item which contains the data that is used to create an Android Notification.
 * All notifications fired by smplrAlarm are without any sound. Notifications in this API were designed to use with alarms which are supposedly have their own
 * sound. However, by setting up the alarm received intent, you can catch the broadcast at the alarm time and use this to play any sound you want for notifications.
 * For more detail please check out the API interface of the same name
 *
 * - channel: A data item which is used to create an Android Notification Channel. For more detailed information please check out the API interface of the same name
 *
 * Optional arguments
 * - requestAPI: API interface to listen the changes on the database. It returns the list of the alarms in JSON format. For more detailed information please check out --> SmplrAlarmListRequestAPI
 * - weekdays: the days of the week on which the alarm is active. An alarm without this argument set rings only once, other alarms however, repeats until they are canceled. To set this parameter the enum class WeekDays must be used.
 * - isActive: state of the alarm which indicates whether alarm is active or not
 * - intent: The intent which is executed when the notification is tapped.
 * - receiverIntent: The full screen intent which is executed when the alarm goes off.
 * - alarmReceivedIntent : The action intent which is executed when the alarm goes off, this can be listened from the app.
 * - info pairs : pairs of strings to pass extra information
 */
fun smplrAlarmSet(context: Context, lambda: SmplrAlarmAPI.() -> Unit): Int =
    SmplrAlarmAPI(context).apply(lambda).setAlarm()

/**
 * API interface for cancelling the alarm.
 * Besides the context in the constructor, it only requires a request code.
 */
fun smplrAlarmCancel(context: Context, lambda: SmplrAlarmAPI.() -> Unit) =
    SmplrAlarmAPI(context).apply(lambda).removeAlarm()

/**
 * API interface for renewing missing alarms.
 * SmplrAlarm automatically deals with intent removing cases namely shutting down the device
 * or changing system clock. However, it can not predict everything which may go wrong. For such cases
 * this function has been created. It simply checks everything in the database against android intent
 * manager, and creates the alarm again based on the information kept in the database.
 */
fun smplrAlarmRenewMissingAlarms(context: Context) =
    SmplrAlarmAPI(context).renewMissingAlarms()

/**
 * API interface for updating the alarm.
 * This function updates the alarms. Besides the context in the constructor,
 * it requires a request code and following update parameters.
 *
 * - Hour
 * - Minute
 * - isActive
 * - weekdays
 * - info pairs
 * - notifications
 */
fun smplrAlarmUpdate(context: Context, lambda: SmplrAlarmAPI.() -> Unit) =
    SmplrAlarmAPI(context).apply(lambda).updateAlarm()

/**
 * API interface to set a database listener.
 * It requires following lambda function
 *
 * (String) -> Unit)
 *
 * this lambda function receives the alarms in the database as JSON formatted text.
 */
fun smplrAlarmChangeOrRequestListener(context: Context, lambda:  ((String) -> Unit)) =
    SmplrAlarmListRequestAPI(context).apply {
        alarmListChangeOrRequestedListener = lambda
    }

/**
 * Data item which holds the following information that accapted and/or required by Android Notification channel
 *
 * importance,showBadge , name, description
 */
fun channel(lambda: ChannelManagerAPI.() -> Unit): NotificationChannelItem =
    ChannelManagerAPI().apply(lambda).build()

/**
 * Data item which holds the following information that accapted and/or required by Android Notification
 *
 * smallIcon, title, message, bigText, autoCancel
 *
 * Additionally holds the following arguments to insert buttons and respective intents which is executed at the click
 *
 * - firstButtonText
 * - secondButtonText
 * - firstButtonIntent
 * - secondButtonIntent
 * - notificationDismissedIntent: The action intent which is executed when the notification is dismissed.
 */
fun alarmNotification(lamda: AlarmNotificationAPI.() -> Unit): NotificationItem =
    AlarmNotificationAPI().apply(lamda).build()