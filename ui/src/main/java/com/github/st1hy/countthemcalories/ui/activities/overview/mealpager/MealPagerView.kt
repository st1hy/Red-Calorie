package com.github.st1hy.countthemcalories.ui.activities.overview.mealpager

import android.app.Activity
import android.content.Context
import android.support.annotation.CheckResult
import android.support.v4.view.ViewPager
import android.widget.TextView
import com.github.st1hy.countthemcalories.ui.R
import com.github.st1hy.countthemcalories.ui.activities.addmeal.model.PhysicalQuantitiesModel
import com.github.st1hy.countthemcalories.ui.contract.DayStatistic
import com.github.st1hy.countthemcalories.ui.inject.app.PerActivity
import com.github.st1hy.countthemcalories.ui.inject.quantifier.context.ActivityContext
import com.jakewharton.rxbinding.support.v4.view.RxViewPager
import kotlinx.android.synthetic.main.overview_app_bar.*
import org.joda.time.format.DateTimeFormat
import rx.Observable
import java.util.*
import javax.inject.Inject


@PerActivity internal class MealPagerView @Inject constructor(private val activity: Activity) {

    @Inject @ActivityContext internal lateinit var context: Context
    @Inject internal lateinit var viewPager: ViewPager
    @Inject internal lateinit var quantitiesModel: PhysicalQuantitiesModel

    internal val  date: TextView = activity.overview_toolbar_date

    fun setCurrentItem(dayPositionInModel: Int, smoothScroll: Boolean) {
        if (viewPager.currentItem != dayPositionInModel) {
            viewPager.setCurrentItem(dayPositionInModel, smoothScroll)
        }
    }

    @CheckResult
    fun onPageSelected(): Observable<Int> {
        return RxViewPager.pageSelections(viewPager)
    }

    fun setTitle(day: DayStatistic) {
        if (day.isToday) {
            date.text = context.getString(R.string.overview_toolbar_title_today)
        } else {
            val dateString = DateTimeFormat.shortDate().print(day.dateTime)
            date.text = String.format(Locale.getDefault(), "%s:", dateString)
        }
        activity.overview_toolbar_total_energy.text = quantitiesModel.formatAsEnergy(day.totalCalories)
    }
}