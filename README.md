# SmplrAlarm
An Android convenience library to make setting an alarm way **simpler**.

[![](https://jitpack.io/v/ColdTea-Projects/SmplrAlarm.svg)](https://jitpack.io/#ColdTea-Projects/SmplrAlarm)

## What and Why?
Android framework has a considerably extensive library with many different alarm types for setting an alarm....but maybe too extensive. It is perfectly reasonable for the Android framework to have an Alarm Manager which supports different types of alarm and is flexible in a way to work with other parts of the framework. But this puts the developers in a position that they need to consume so much time and manage with so many different parts of the framework (database, broadcast receivers, Calendar etc.) to make, maybe the one of the simplest type of mobile app: An alarm clock.

For so many cases, applications which needs to set a consistent alarm (for a simple alarm clock or for other usages such as an app reminds you to drink water, take your pills etc.) do not require anything different than each other, however as it is mentioned above, it takes some time and requires some knowledge about different android libraries.

SmplrAlarm manages all that necessery modules to set a proper alarm by using native android libraries, provides an API interface powered by Kotlin DSL and at the end of the day makes setting an alarm as simple as:

	smplrAlarmSet(applicationContext) {
            hour { hour }
            min { minute }
        }


## How to install: 

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
  
## How to use:

### Setting an alarm:

All that SmplrAlarm requires to set an alarm is an integer reperesenting hour and another integer representing minute:

	smplrAlarmSet(applicationContext) {
            hour { hour }
            min { minute }
        }

Just like that, you set a one-time alarm with a dummy notification to see how simple to set an alarm with SmplrAlarm. Now, let's make
it more useful.

### Repeating alarm:

The repeating alarm can be set by adding the weekdays you want:

	smplrAlarmSet(applicationContext) {
            hour { hour }
            min { minute }
            weekdays {
                monday()
                friday()
                sunday()
            }
        }

### Adding a notification and Notification channel:

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
	
Notifications also can be created with up to two buttons just by sending the button text and click intent by following steps.

Step-1: Create intents:

        val snoozeIntent = Intent(applicationContext, ActionReceiver::class.java).apply {
            action = ACTION_SNOOZE
            putExtra(HOUR, hour)
            putExtra(MINUTE, minute)
        }

        val dismissIntent = Intent(applicationContext, ActionReceiver::class.java).apply {
            action = ACTION_DISMISS
        }
	
Step-2: Implement them in the scope: channel{}

	channel {
	    ...
	    firstButtonText { "Snooze" }
	    secondButtonText { "Dismiss" }
	    firstButtonIntent { snoozeIntent }
	    secondButtonIntent { dismissIntent }
	}
	
### Adding intents:






	


