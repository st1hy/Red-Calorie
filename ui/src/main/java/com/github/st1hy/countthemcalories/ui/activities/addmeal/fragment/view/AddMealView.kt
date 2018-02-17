package com.github.st1hy.countthemcalories.ui.activities.addmeal.fragment.view

import android.content.Context
import android.support.annotation.CheckResult
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.github.st1hy.countthemcalories.ui.activities.addmeal.view.AddMealScreen
import com.github.st1hy.countthemcalories.ui.core.state.Visibility
import com.github.st1hy.countthemcalories.ui.core.time.RxTimePicker
import com.github.st1hy.countthemcalories.ui.inject.app.PerFragment
import com.github.st1hy.countthemcalories.ui.inject.quantifier.context.ActivityContext
import com.github.st1hy.countthemcalories.ui.inject.quantifier.view.FragmentRootView
import com.jakewharton.rxbinding.view.RxView
import com.jakewharton.rxbinding.widget.RxTextView
import kotlinx.android.synthetic.main.add_meal_content.view.*
import org.joda.time.DateTime
import rx.Observable
import javax.inject.Inject


interface AddMealView : AddMealScreen {

    @CheckResult
    fun getNameObservable(): Observable<CharSequence>

    @CheckResult
    fun mealTimeClicked(): Observable<Void>

    @CheckResult
    fun openTimePicker(currentTime: DateTime): Observable.Transformer<Void, DateTime>

    fun setName(name: String)

    fun setEmptyIngredientsVisibility(visibility: Visibility)

    fun scrollTo(itemPosition: Int)

    fun setTotalEnergy(totalEnergy: String)

    fun setHint(mealNameNow: String)

    fun setMealTime(time: String)
}


@PerFragment
class AddMealViewController @Inject constructor(
        @FragmentRootView private val rootView: View,
        screen: AddMealScreen,
        @ActivityContext private val context: Context
) : AddMealScreen by screen, AddMealView {

    private val name: EditText = rootView.add_meal_name
    private val mealTime: Button = rootView.add_meal_time_value

    override fun setName(name: String) {
        this.name.setText(name)
        this.name.setSelection(name.length)
    }

    override fun getNameObservable(): Observable<CharSequence> {
        return RxTextView.textChanges(name)
    }

    override fun setEmptyIngredientsVisibility(visibility: Visibility) {
        rootView.add_meal_empty_ingredients.visibility = visibility.visibility
    }

    override fun scrollTo(itemPosition: Int) {
        rootView.add_meal_ingredients_list.scrollToPosition(itemPosition)
    }

    override fun setTotalEnergy(totalEnergy: String) {
        rootView.add_meal_total_calories.text = totalEnergy
    }

    override fun setHint(mealNameNow: String) {
        name.hint = mealNameNow
    }

    override fun mealTimeClicked(): Observable<Void> {
        return RxView.clicks(mealTime)
    }

    override fun setMealTime(time: String) {
        mealTime.text = time
    }

    override fun openTimePicker(currentTime: DateTime) = VoidToDateTime { void ->
        void.flatMap { return@flatMap RxTimePicker.openPicker(context, currentTime) }
    }
}

private typealias VoidToDateTime = Observable.Transformer<Void, DateTime>