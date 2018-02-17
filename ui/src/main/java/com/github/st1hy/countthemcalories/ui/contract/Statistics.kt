package com.github.st1hy.countthemcalories.ui.contract

import android.os.Parcelable
import org.joda.time.DateTime


interface CalorieStatistics : Parcelable{
    val min: Float
    val max: Float
    val average: Float
    val median: Float
    val minWeight: Float
    val maxWeight: Float
    val daysCount: Int
    val data: List<DayStatistic>
    fun getDayDataAt(position: Int): DayStatistic
    fun findDayPosition(dateTime: DateTime): Int
}

interface DayStatistic : Parcelable{
    val dateTime: DateTime
    val totalCalories: Float
    val weight: Float
    val hasAnyData: Boolean
    val isToday: Boolean
    fun isDay(atDay: DateTime): Boolean
}
