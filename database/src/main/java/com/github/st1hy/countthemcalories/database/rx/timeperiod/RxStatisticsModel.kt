package com.github.st1hy.countthemcalories.database.rx.timeperiod

import android.database.Cursor
import android.support.annotation.CheckResult
import com.github.st1hy.countthemcalories.contract.model.CalorieStatistics
import com.github.st1hy.countthemcalories.contract.model.DayStatistic
import com.github.st1hy.countthemcalories.database.*
import com.github.st1hy.countthemcalories.database.rx.AbstractRxDatabaseModel
import com.google.common.collect.ImmutableList
import org.greenrobot.greendao.query.CursorQuery
import org.joda.time.DateTime
import org.joda.time.Days
import rx.Observable
import rx.subjects.PublishSubject
import java.util.*


class TimePeriodModel : AbstractRxDatabaseModel() {

    private val queryProvider by lazy {
        val sql = "SELECT " +
                "M." + MealDao.Properties.CreationDate.columnName + ", " +
                "I." + IngredientDao.Properties.Amount.columnName + ", " +
                "IT." + IngredientTemplateDao.Properties.EnergyDensityAmount.columnName + ", " +
                "from " + MealDao.TABLENAME + " M " +
                "join " + IngredientDao.TABLENAME + " I " +
                "on I." + IngredientDao.Properties.PartOfMealId.columnName +
                " = M." + MealDao.Properties.Id.columnName + " " +
                "join " + IngredientTemplateDao.TABLENAME + " IT " +
                "on I." + IngredientDao.Properties.IngredientTypeId.columnName +
                " = IT." + IngredientTemplateDao.Properties.Id.columnName + " " +
                "where M." + MealDao.Properties.CreationDate.columnName + " " +
                "between " + "(?) and (?) " +
                "order by M." + MealDao.Properties.CreationDate.columnName + " asc;"
        CursorQuery.internalCreate(dao(), sql, arrayOfNulls(2))
    }

    private val weightQueryProvider by lazy {
        session().weightDao
                .queryBuilder()
                .where(WeightDao.Properties.MeasurementDate.between(null, null))
                .orderAsc(WeightDao.Properties.MeasurementDate)
                .build()
    }

    private var start: DateTime? = null
    private var end: DateTime? = null
    private val updates = PublishSubject.create<CalorieStatistics>()

    @CheckResult
    fun updates(): Observable<CalorieStatistics> {
        return updates.asObservable()
    }

    fun refresh() {
        if (start != null && end != null) {
            refresh(start!!, end!!)
        }
    }

    fun refresh(start: DateTime, end: DateTime) {
        this.start = start
        this.end = end
        loadData(start, end).subscribe({ updates.onNext(it) })
    }

    @CheckResult
    private fun loadData(start: DateTime,
                 end: DateTime
    ): Observable<CalorieStatistics> {
        return fromDatabaseTask {
            val query = queryProvider.forCurrentThread()
            query.setParameter(0, start.millis)
            query.setParameter(1, end.millis)
            val cursor = query.query()
            val weightQuery = weightQueryProvider.forCurrentThread()
            weightQuery.setParameter(0, start.millis - 1)
            weightQuery.setParameter(1, end.millis + 1)
            val list = weightQuery.listLazy()
            cursor.use {
                return@fromDatabaseTask Builder(it, start, end, list).build()
            }
        }
    }

    private fun dao(): MealDao {
        return session().mealDao
    }

    private class Builder internal constructor(private val cursor: Cursor,
                                               private val start: DateTime,
                                               end: DateTime,
                                               private val weightList: List<Weight>) {
        private val daysCount: Int = Days.daysBetween(start, end).days
        private val data = ImmutableList.builder<DayData>()
        private var loaded = false
        private var min = Float.MAX_VALUE
        private var max = Float.MIN_VALUE
        private var average = 0f
        private var median = 0f
        private var minWeight = Float.MAX_VALUE
        private var maxWeight = Float.MIN_VALUE
        private val medianCaloriesData: FloatArray = FloatArray(daysCount)
        private var notZeroCalorieDays = 0

        private var hasAnyData: Boolean = false
        private var positionLoadedButNotUsed: Boolean = false
        private var weightIterator: Iterator<Weight>? = null
        private var weight: Weight? = null


        fun build(): CalorieStatistics {
            if (!loaded) {
                loadAll()
                loaded = true
            }
            return TimePeriod(data.build(), min, max, average, median, minWeight, maxWeight)
        }

        private fun loadAll() {
            positionLoadedButNotUsed = false
            weight = null
            weightIterator = weightList.iterator()
            for (i in 0 until daysCount) {
                hasAnyData = false
                val start = this.start.plusDays(i)
                val end = this.start.plusDays(i + 1)
                val amount = getAmount(end)
                val weightValue = getWeight(end)
                val dayData = DayData(start, amount, weightValue, hasAnyData)
                updateStatistics(dayData, weightValue)
                data.add(dayData)
            }
            computeAverageAndMedian()
        }

        private fun getAmount(end: DateTime): Float {
            var amount = 0.0
            while (positionLoadedButNotUsed || cursor.moveToNext()) {
                positionLoadedButNotUsed = true
                val timestamp = cursor.getLong(POSITION_TIME)
                val time = DateTime(timestamp)
                if (time.isAfter(end)) {
                    break
                }
                positionLoadedButNotUsed = false
                hasAnyData = true
                amount += getAmount(cursor)
            }
            return amount.toFloat()
        }

        private fun getWeight(end: DateTime): Float {
            var weightValue = 0f
            while (weight != null || weightIterator!!.hasNext()) {
                if (weight == null) weight = weightIterator!!.next()
                val measurementDate = weight!!.measurementDate
                if (measurementDate.isEqual(end) || measurementDate.isAfter(end)) {
                    break
                }
                hasAnyData = true
                weightValue = weight!!.weight
                weight = null
            }
            return weightValue
        }

        private fun updateStatistics(data: DayStatistic, weightValue: Float) {
            val value = data.totalCalories
            if (value < min) min = value
            if (value > max) max = value
            if (value > 0) {
                average += value
                medianCaloriesData[notZeroCalorieDays] = value
                notZeroCalorieDays++
            }
            if (weightValue > 0f) {
                if (weightValue < minWeight) minWeight = weightValue
                if (weightValue > maxWeight) maxWeight = weightValue
            }
        }

        private fun computeAverageAndMedian() {
            if (notZeroCalorieDays > 0) {
                average /= notZeroCalorieDays.toFloat()
                Arrays.sort(medianCaloriesData, 0, notZeroCalorieDays)
                median = median(medianCaloriesData, 0, notZeroCalorieDays)
            }
        }
    }

    companion object {

        private const val POSITION_TIME = 0
        private const val POSITION_AMOUNT = 1
        private const val POSITION_ENERGY_DENSITY = 2

        @JvmOverloads fun median(m: FloatArray, from: Int = 0, to: Int = m.size): Float {
            if (from > to || from < 0 || to < 0) throw IllegalArgumentException("Incorrect range")
            val length = to - from
            if (length == 0) return 0f
            if (length == 1) return m[from]
            val middle = from + length / 2
            return if (length % 2 == 1) {
                m[middle]
            } else {
                (m[middle - 1] + m[middle]) / 2.0f
            }
        }

        @JvmStatic
        fun getAmount(cursor: Cursor): Double {
            val energyDensityValue = cursor.getDouble(POSITION_ENERGY_DENSITY)
            val currentAmount = cursor.getDouble(POSITION_AMOUNT)
            return energyDensityValue * currentAmount
        }
    }
}
