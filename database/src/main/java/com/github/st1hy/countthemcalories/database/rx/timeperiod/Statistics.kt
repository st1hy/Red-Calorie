package com.github.st1hy.countthemcalories.database.rx.timeperiod

import org.joda.time.DateTime
import org.parceler.Parcel
import org.parceler.ParcelConstructor

@Parcel(Parcel.Serialization.BEAN)
data class TimePeriod @ParcelConstructor internal constructor(
        val data: List<DayData>,
        val min: Float,
        val max: Float,
        val average: Float,
        val median: Float,
        val minWeight: Float,
        val maxWeight: Float
) {

    val daysCount: Int
        get() = data.size

    fun getDayDataAt(position: Int): DayData {
        return data[position]
    }

    fun findDayPosition(dateTime: DateTime): Int {
        for (i in data.indices.reversed()) {
            val dayData = data[i]
            if (dayData.isDay(dateTime)) {
                return i
            }
        }
        return daysCount - 1
    }
}

@Parcel(Parcel.Serialization.BEAN)
data class DayData @ParcelConstructor internal constructor(
        val dateTime: DateTime,
        val totalCalories: Float,
        val weight: Float,
        val hasAnyData: Boolean
) {

    val isToday: Boolean
        get() = isDay(DateTime.now())

    fun isDay(atDay: DateTime): Boolean {
        val dayStart = atDay.withTimeAtStartOfDay()
        val nextDayStart = atDay.plusDays(1).withTimeAtStartOfDay()
        return dateTime.isEqual(dayStart) || dateTime.isAfter(dayStart) && dateTime.isBefore(
                nextDayStart)
    }
}