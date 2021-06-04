# SmplrAlarm
An Android convenience library to make setting an alarm way **simpler**.

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
                    importance { NotificationManager.IMPORTANCE_HIGH },
                    showBadge { false },
                    name { "de.coldtea.smplr.alarm.channel" },
                    description { "this notification channel is created by SmplrAlar" }
                }
            }
	    
        }
	
Notifications also can be created with up to two buttons just by sending the button text and click intent. Let's add the classic alarm notification buttons snooze and dismiss by following the steps below:

Step-1: Create the intents

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
	
Simple as that!
