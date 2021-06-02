package de.coldtea.smplr.smplralarm.apis

import de.coldtea.smplr.smplralarm.models.WeekDays

/**
 * Created by [Yasar Naci Gündüz](https://github.com/ColdTea-Projects).
 */
class WeekDaysAPI {

    private var monday = false
    private var tuesday = false
    private var wednesday = false
    private var thursday = false
    private var friday = false
    private var saturday = false
    private var sunday = false

    fun monday() {
        this.monday = true
    }

    fun tuesday() {
        this.tuesday = true
    }

    fun wednesday() {
        this.wednesday = true
    }

    fun thursday() {
        this.thursday = true
    }

    fun friday() {
        this.friday = true
    }

    fun saturday() {
        this.saturday = true
    }

    fun sunday() {
        this.sunday = true
    }

    fun getWeekDays(): List<WeekDays> = mutableListOf<WeekDays>().also {
        if (sunday) it.add(WeekDays.SUNDAY)
        if (monday) it.add(WeekDays.MONDAY)
        if (tuesday) it.add(WeekDays.TUESDAY)
        if (wednesday) it.add(WeekDays.WEDNESDAY)
        if (thursday) it.add(WeekDays.THURSDAY)
        if (friday) it.add(WeekDays.FRIDAY)
        if (saturday) it.add(WeekDays.SATURDAY)
    }.sortedBy { it.ordinal }.toList()

}