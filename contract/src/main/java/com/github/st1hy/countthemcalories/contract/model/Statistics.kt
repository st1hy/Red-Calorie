package com.github.st1hy.countthemcalories.contract.model

import org.joda.time.DateTime


interface CalorieStatistics {

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

interface DayStatistic {
    val dateTime: DateTime

    val totalCalories: Float

    val weight: Float

    val hasAnyData: Boolean

    val isToday: Boolean

    fun isDay(atDay: DateTime): Boolean
}
