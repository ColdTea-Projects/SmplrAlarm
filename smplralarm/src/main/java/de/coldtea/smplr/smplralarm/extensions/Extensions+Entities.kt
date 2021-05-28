package de.coldtea.smplr.smplralarm.extensions

import de.coldtea.smplr.smplralarm.models.NotificationItem
import de.coldtea.smplr.smplralarm.repository.entity.NotificationEntity

internal fun NotificationEntity.convertToNotificationItem() =
    NotificationItem(
        smallIcon,
        title,
        message,
        bigText,
        autoCancel,
        if(firstButton.isNotEmpty()) firstButton else null,
        if(secondButton.isNotEmpty()) secondButton else null,
        null,
        null
    )