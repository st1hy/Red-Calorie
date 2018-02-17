package com.github.st1hy.countthemcalories.ui.activities.overview.view

import android.app.Activity
import android.content.Intent
import android.support.annotation.CheckResult
import android.support.v4.app.ActivityOptionsCompat
import android.view.MotionEvent
import com.getbase.floatingactionbutton.FloatingActionsMenu
import com.github.st1hy.countthemcalories.ui.activities.addmeal.AddMealActivity
import com.github.st1hy.countthemcalories.ui.activities.addmeal.CopyMealActivity
import com.github.st1hy.countthemcalories.ui.activities.addmeal.EditMealActivity
import com.github.st1hy.countthemcalories.ui.activities.mealdetail.MealDetailActivity
import com.github.st1hy.countthemcalories.ui.activities.overview.model.MealDetailAction
import com.github.st1hy.countthemcalories.ui.activities.overview.model.MealDetailAction.*
import com.github.st1hy.countthemcalories.ui.activities.overview.model.MealDetailParams
import com.github.st1hy.countthemcalories.ui.contract.Meal
import com.github.st1hy.countthemcalories.ui.core.activityresult.ActivityLauncher
import com.github.st1hy.countthemcalories.ui.core.activityresult.ActivityResult
import com.github.st1hy.countthemcalories.ui.core.activityresult.StartParams
import com.github.st1hy.countthemcalories.ui.inject.app.PerActivity
import com.jakewharton.rxbinding.view.RxView
import kotlinx.android.synthetic.main.overview_fab.*
import org.parceler.Parcels
import rx.Observable
import rx.functions.Func1
import javax.inject.Inject


internal interface OverviewScreen {

    fun isFabMenuOpen(): Boolean
    @CheckResult
    fun openMealDetails(): Observable.Transformer<MealDetailParams, MealDetailAction>

    fun editMeal(meal: Meal)
    @CheckResult
    fun touchOverlay(handled: Func1<in MotionEvent, Boolean>): Observable<MotionEvent>

    fun closeFloatingMenu()
    fun copyMeal(meal: Meal)
}

private typealias ParamsToAction = Observable.Transformer<MealDetailParams, MealDetailAction>

@PerActivity internal class OverviewScreenImpl @Inject constructor(
        private val activity: Activity,
        private val activityLauncher: ActivityLauncher
) : OverviewScreen {

    private var fabMenu: FloatingActionsMenu = activity.overview_fab_menu

    override fun openMealDetails() = ParamsToAction { paramsObservable ->
        paramsObservable.map(this::getMealDetailsParams)
                .compose(activityLauncher.startActivityForResult(REQUEST_MEAL_DETAIL))
                .map(this::getMealDetailResult)
    }

    private fun getMealDetailsParams(params: MealDetailParams): StartParams {
        val startOptions = ActivityOptionsCompat
                .makeSceneTransitionAnimation(activity, params.sharedView,
                        "overview-shared-view-image")
                .toBundle()
        val intent = Intent(activity, MealDetailActivity::class.java)
        intent.putExtra(MealDetailActivity.EXTRA_MEAL_PARCEL, Parcels.wrap(params.meal))
        return StartParams.of(intent, REQUEST_MEAL_DETAIL, startOptions)
    }

    override fun editMeal(meal: Meal) {
        val intent = Intent(activity, EditMealActivity::class.java)
        intent.putExtra(AddMealActivity.EXTRA_MEAL_PARCEL, Parcels.wrap(meal))
        activity.startActivity(intent)
    }

    override fun touchOverlay(handled: Func1<in MotionEvent, Boolean>): Observable<MotionEvent> {
        return RxView.touches(activity.overview_touch_overlay, handled)
    }

    override fun closeFloatingMenu() {
        fabMenu.collapse()
    }

    override fun isFabMenuOpen(): Boolean {
        return fabMenu.isExpanded
    }

    override fun copyMeal(meal: Meal) {
        val intent = Intent(activity, CopyMealActivity::class.java)
        intent.putExtra(AddMealActivity.EXTRA_MEAL_PARCEL, Parcels.wrap(meal))
        activity.startActivity(intent)
    }

    private fun getMealDetailResult(activityResult: ActivityResult): MealDetailAction {
        val data = activityResult.data ?: return CANCELED
        val mealId = data.getLongExtra(MealDetailActivity.EXTRA_RESULT_MEAL_ID_LONG, -2L)
        if (mealId == -2L) return CANCELED
        return when (activityResult.resultCode) {
            MealDetailActivity.RESULT_EDIT -> create(Type.EDIT, mealId)
            MealDetailActivity.RESULT_DELETE -> create(Type.DELETE, mealId)
            MealDetailActivity.RESULT_COPY -> create(Type.COPY, mealId)
            else -> CANCELED
        }
    }

    companion object {
        private const val REQUEST_MEAL_DETAIL = 0x300
    }
}