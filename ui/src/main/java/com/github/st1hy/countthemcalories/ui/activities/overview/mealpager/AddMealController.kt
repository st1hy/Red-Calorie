package com.github.st1hy.countthemcalories.ui.activities.overview.mealpager

import android.app.Activity
import android.content.Intent
import android.support.annotation.CheckResult
import com.github.st1hy.countthemcalories.ui.activities.addmeal.AddMealActivity
import com.github.st1hy.countthemcalories.ui.activities.overview.view.OverviewScreen
import com.github.st1hy.countthemcalories.ui.inject.app.PerActivity
import com.jakewharton.rxbinding.view.RxView
import kotlinx.android.synthetic.main.overview_fab.*
import org.joda.time.DateTime
import rx.Observable
import javax.inject.Inject

@PerActivity
internal class AddMealController @Inject constructor(private val activity: Activity) {

    @Inject lateinit var pagerModel: PagerModel
    @Inject lateinit var screen: OverviewScreen

    private val addMealClicks by lazy { RxView.clicks(activity.overview_fab_add_meal).share() }

    @CheckResult
    fun getAddNewMealObservable(atDay: DateTime): Observable<Void> =
            addMealClicks.filter { selectedDayMatches(atDay) }

    private fun selectedDayMatches(atDay: DateTime): Boolean {
        val selectedDay = pagerModel.selectedDay
        return selectedDay != null && atDay == selectedDay.dateTime
    }

    fun addNewMeal(atDay: DateTime) {
        val intent = Intent(activity, AddMealActivity::class.java)
        intent.putExtra(AddMealActivity.EXTRA_NEW_MEAL_DATE, atDay)
        activity.startActivity(intent)
    }

    fun closeFloatingMenu() {
        screen.closeFloatingMenu()
    }
}
