# SmplrAlarm
An Android convenience library to make setting an alarm way **simpler** than it is.

[![](https://jitpack.io/v/ColdTea-Projects/SmplrAlarm.svg)](https://jitpack.io/#ColdTea-Projects/SmplrAlarm)

## What and Why?
Android framework has a considerably extensive library with many different alarm types for setting an alarm....but maybe too extensive. It is perfectly reasonable for the Android framework to have an Alarm Manager that supports different types of alarm and is flexible to work with other parts of the framework. But this puts the developers in a position that they need to consume so much time and manage with so many different parts of the framework (database, broadcast receivers, Calendar etc.) to make, maybe one of the simplest type of mobile app: An alarm clock.

For so many cases, applications that need to set a consistent alarm (for a simple alarm clock or for other usages such as an app that reminds you to drink water, take your pills etc.) do not require anything different from each other, however as it is mentioned above, it takes some time and requires some knowledge about different android libraries and the developer needs to deal with various situations (restart the device, change the date/time settings) to make sure that the alarms are consistent.

SmplrAlarm manages all the necessary modules to set a proper alarm by using native android libraries, provides an API interface powered by Kotlin DSL and at the end of the day makes setting an alarm as simple as:

	smplrAlarmSet(applicationContext) {
            hour { hour }
            min { minute }
        }


## How to install

Gradle 

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

	dependencies {
	        implementation 'com.github.ColdTea-Projects:SmplrAlarm:v1.0.0'
	}
  
Maven
  
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
  

	<dependency>
	    <groupId>com.github.ColdTea-Projects</groupId>
	    <artifactId>SmplrAlarm</artifactId>
	    <version>Tag</version>
	</dependency>
	
**Warning:** Library requires minimum SDK version 24

## ChangeLog
### [2.0.2] 11.12.2021

1. Android 12 integration
2. Java 11 integration
3. Kotlin 1.5.31
4. compileSdkVersion 31
5. Dependency updates:
	  - org.jetbrains.kotlin:kotlin-stdlib:1.6.0
	  - androidx.core:core-ktx:1.7.0
	  - androidx.appcompat:appcompat:1.4.0
	  - com.google.android.material:material:1.4.0
	  - androidx.test.ext:junit:1.1.3
	  - com.android.tools.build:gradle:7.0.3
	  - com.google.gms:google-services:4.3.10
	  - koin_version 3.1.4
6. Updateable notifications
7. Notifications are muted

### [v1.3.0] 12.11.2021
Added
notificationDismissedIntent : The action intent which is executed when the notification is dismissed.

### [v1.2.0] 31.10.2021
### Added
- notificationReceivedIntent: Intent to receive the broadcast which is send when the alarm is fired.
### Fixed
- JSON conversion error on converting extrasKeySet of full screen intent. It is possible now to sen extras in format of String, Int, Long and Double

### [v1.1.0] 10.07.2021
### Added

- infoPairs: New field in the alarm object which is used to pass information
- Full screen intents also returns request id

### [v1.0.0] 02.06.2021

- Set, Update, Delete alarm
- Listen and get saved alarms
- An alarm can consist of : hour, minute, weekdays, notification, notification channel, notification buttons and intents (max.29, intent, full screen intent, activity state.


 
## How to use

### Setting an alarm

All that SmplrAlarm requires to set an alarm is an integer reperesenting the hour and another integer representing the minute:

	smplrAlarmSet(applicationContext) {
            hour { hour }
            min { minute }
        }

Just like that, you set a one-time alarm with a dummy notification to see how simple to set an alarm with SmplrAlarm. Now, let's make
it more useful.

### Repeating alarm

The repeating alarm can be set by initiating the weekdays you want your alarm to ring on:

	smplrAlarmSet(applicationContext) {
            hour { hour }
            min { minute }
            weekdays {
                monday()
                friday()
                sunday()
            }
        }

### Adding a notification and Notification channel

	smplrAlarmSet(applicationContext) {
            hour { hour }
            min { minute }
            weekdays {
                monday()
                friday()
                sunday()
            }
            notification {
                alarmNotification {
                    smallIcon { R.drawable.ic_baseline_alarm_on_24 }
                    title { "Simple alarm is ringing" }
                    message { "Simple alarm is ringing"}
                    bigText { "Simple alarm is ringing"}
                    autoCancel { true }
                }
            }
            notificationChannel {
                channel {
                    importance { NotificationManager.IMPORTANCE_HIGH }
                    showBadge { false }
                    name { "de.coldtea.smplr.alarm.channel" }
                    description { "this notification channel is created by SmplrAlar" }
                }
            }
	    
        }
	
Notifications also can be created with up to two buttons just by sending the button text and click intent. Let's add the classic alarm notification buttons snooze and dismiss by following the steps below:

Step-1: Create the intents (the class ActionReceiver in the snippet needs to be created as a sub-class of BroadcastReceiver)

        val snoozeIntent = Intent(applicationContext, ActionReceiver::class.java).apply {
            action = ACTION_SNOOZE
            putExtra(HOUR, hour)
            putExtra(MINUTE, minute)
        }

        val dismissIntent = Intent(applicationContext, ActionReceiver::class.java).apply {
            action = ACTION_DISMISS
        }
	
Step-2: Implement them in the scope: alarmNotification{}

	alarmNotification {
	    ...
	    firstButtonText { "Snooze" }
	    secondButtonText { "Dismiss" }
	    firstButtonIntent { snoozeIntent }
	    secondButtonIntent { dismissIntent }
	}

Intents here that we require conveys a broadcast receiver. Please consult the following document to learn more about Broadcast Receivers:

https://developer.android.com/guide/components/broadcasts

!!! Notifications are updated in version 2.0.2. Please refer the section which lists the updates in version 2.0.2 !!!
	
### Adding intents

SmplrAlarm offers to set Intents in motion for two events:

1- When the alarm notification is clicked, it opens the Activity stated in the intent.

2- When the alarm rings, it starts the activity stated in the receiverIntent.

let's create and add the intents as in the following snippet:

        val onClickShortcutIntent = Intent(
            applicationContext,
            MainActivity::class.java
        )

        val fullScreenIntent = Intent(
            applicationContext,
            ActivityLockScreenAlarm::class.java
        )
	
	smplrAlarmSet(applicationContext) {
            ...
            intent { onClickShortcutIntent }
            receiverIntent { fullScreenIntent }
	    ...
    	}
	
Receiver intent is designed to be shown in the lock screen. It can be used for other purposes too but if you intend to use it as the alarm screen on the lock screen please check the sample activity (ActivityLockScreenAlarm) in the demo app.

### Alarm id

SmplrAlarm library produces and returns a unique id based on time, everytime the smplrAlarmSet() is called. This id represents the alarms kept in database as well as the id of the notification thorwn. Please observe the ActionReceiver class of the demo app, how the same id used to cancel the notification.

### Update an alarm

Update function supports only changing hour, minute, weekdays and whether the alarm is active. For adding more changes, alarm needs to be cancelled and reset. 

	smplrAlarmUpdate(applicationContext) {
            requestCode { requestCode }
            hour { 19 }
            min { 23 }
            weekdays {
                friday()
            }
            isActive { isActive }
        }
	
!!! Notifications are too updatable from version 2.0.2 on. Please refer the section which lists the updates in version 2.0.2 !!!

### Cancel an alarm

        smplrAlarmCancel(applicationContext) {
            requestCode { requestCode }
        }
	
### Listening the database

Last but not the least, we provide you the information of all the alarms set by SmplrAlarm. Since the database operations require an async process, one has to wait until the query response arrives. SmplrAlarm takes care of everything in that regard and only asks for a listener to return alarm info in JSON format.

To do it so firstly we need to implement an instance of the API which allows us to request the list of alarms with the listener

	var smplrAlarmListRequestAPI: SmplrAlarmListRequestAPI = smplrAlarmChangeOrRequestListener(applicationContext) { jsonString ->
            ...
	    whatever you want to do with the alarm list in json format
	    ...
        }
	
Just like that it is set, now we can make our request:


	smplrAlarmListRequestAPI.requestAlarmList()
	
And when the query is done, you get the list in following format:

	{
	"alarmItems":[
			{"requestId":745503894, "hour":22, "minute": 29, "weekdays": ["SUNDAY", "THURSDAY"], "isActive": true},
			{"requestId":745503894, "hour":0, "minute": 9, "weekdays": ["SUNDAY", "WEDNESDAY"], "isActive": true}
		     ]
	}
	
If you just created the listener, you can ask for the updates, however if you do that only manually, you can ask for th updates before updates happen. Every data manupilation process in SmplrAlarm (create, update, delete) contains a call to the listener, all you need to do is sending your listener inside:

	smplrAlarmSet(applicationContext) {
		    ...
		    requestAPI { smplrAlarmListRequestAPI }
		    ...
		}
		
### Getting notifications

When the alarm rings, if it is set, system fires a notification which has the same id with the alarm in the database. SmplrAlarm provides this id in the intent that you recieve on your Broadcast receiver that you set with button intents. All you need to do is :

	requestId = intent.getIntExtra(SmplrAlarmAPI.SMPLR_ALARM_NOTIFICATION_ID, -1)
		
## (New) v1.1.0 and above:

### Getting alarms on full screen intent

When the alarm rings and opens a full screen activity, SmplrAlarm provides the alarm id in the intent that you recieve on your full screen activity. All you need to do is :

	requestId = intent.getIntExtra(SmplrAlarmAPI.SMPLR_ALARM_REQUEST_ID, -1)

### infoPairs

No matter how many more fields we add to the smplrAlarm we can not cover everything and only violate our principle of keeping it simple. However this does not change the fact that at some point users may need to keep information in the database. For this reason we added infoPairs. infoPairs is a new field that we can send pairs of string in List format:

	val extraInformation = listOf("snoozeTime" to "19:15", 
				      "note" to "take your pills")
				
	
	smplrAlarmSet(applicationContext) {
		    ...
		    infoPairs { extraInformation }
		}
		
When the alarm rings, intents as well as notifications provides the requestId. Using this request id you can easily get the alarm from the database (see section *Listening the database*) and use the infoPairs that you saved.


Simple as that!

## (New) v1.2.0 and above:

### Catching alarms when they start ringing

When the alarm rings on LockScreen SmplrAlarm fires the Activity which was assinged to the full screen intent and otherwise just a notification. Alongside this two visual action, SmplrAlarm also sends a broadcast with the BroadcastReceiver intent which has been assigned to _alarmReceivedIntent_ 


	val alarmReceivedIntent = Intent(
	    applicationContext,
	    AlarmBroadcastReceiver::class.java // this class must be inherited from BroadcastReceiver
	)
	
	smplrAlarmSet(applicationContext) {
	    ...
	    intent { onClickShortcutIntent }
	    alarmReceivedIntent { alarmReceivedIntent }
	    ...
	}
	
Later when the alarm is fired, onReceive method of AlarmBroadcastReceiver will be called. In this class you can get alarmId as follows:

	override fun onReceive(context: Context?, intent: Intent?) {
	        requestId = intent?.getIntExtra(SmplrAlarmAPI.SMPLR_ALARM_REQUEST_ID, -1)?:return
	        ...
	    }
    
With this broadccast receiver, you can react when the alarm stars ringing, simple as that!

## (New) v1.3.0 and above:

### Listen notification dismissal

Notification dismissal can be listened by implementing following steps:

Step-1: Create the intent (the class ActionReceiver in the snippet needs to be created as a sub-class of BroadcastReceiver)

        val notificationDismissIntent = Intent(applicationContext, ActionReceiver::class.java).apply {
            action = ACTION_NOTIFICATION_DISMISS
        }
	
Step-2: Implement them in the scope: alarmNotification{}

	alarmNotification {
	    ...
	    notificationDismissedIntent { notificationDismissIntent }
	}

After these steps are implemented, your ActionReceiver class will receive an action with key ACTION_NOTIFICATION_DISMISS when the user dismisses the notification, simple as that!

## (New) 2.0.2 and above:

### Now notifications are muted: 

All notifications fired by smplrAlarm are without any sound from this version on. Notifications in this API were designed to use with alarms which are supposedly have their own sound. However, by setting up the alarm received intent, you can catch the broadcast at the alarm time and use this to play any sound you want for notifications.

### Updatable notifications

SmplrAlarm is even more flexible now! Version 2.0.2 and above now supports updating the notifications. The usage is exactly same with smplrAlarmSet(). All you need to do is :

	smplrAlarmUpdate(requireContext().applicationContext) {
			requestCode { binding.alarmId.text.toString().toInt() }
			.
			.
			notification {
			    NotificationItem(
				smallIcon = R.drawable.ic_baseline_change_circle_24,
				title = "I am changed",
				message = "I am changed",
				bigText = "I am changed",
				...
				..
				.
			    )
			}
			.
			.
		    }
		    
Simple as that!	    
		   


