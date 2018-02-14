package com.github.st1hy.countthemcalories.database.rx.timeperiod

import com.github.st1hy.countthemcalories.contract.model.CalorieStatistics
import com.github.st1hy.countthemcalories.contract.model.DayStatistic
import org.joda.time.DateTime
import org.parceler.Parcel
import org.parceler.ParcelConstructor

@Parcel(Parcel.Serialization.BEAN)
data class TimePeriod @ParcelConstructor internal constructor(
        override val data: List<DayData>,
        override val min: Float,
        override val max: Float,
        override val average: Float,
        override val median: Float,
        override val minWeight: Float,
        override val maxWeight: Float
) : CalorieStatistics {

    override val daysCount: Int
        get() = data.size

    override fun getDayDataAt(position: Int): DayStatistic {
        return data[position]
    }

    override fun findDayPosition(dateTime: DateTime): Int {
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
        override val dateTime: DateTime,
        override val totalCalories: Float,
        override val weight: Float,
        override val hasAnyData: Boolean
) : DayStatistic {

    override val isToday: Boolean
        get() = isDay(DateTime.now())

    override fun isDay(atDay: DateTime): Boolean {
        val dayStart = atDay.withTimeAtStartOfDay()
        val nextDayStart = atDay.plusDays(1).withTimeAtStartOfDay()
        return dateTime.isEqual(dayStart) || dateTime.isAfter(dayStart) && dateTime.isBefore(
                nextDayStart)
    }
}