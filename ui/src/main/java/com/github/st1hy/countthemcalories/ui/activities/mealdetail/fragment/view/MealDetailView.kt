package com.github.st1hy.countthemcalories.ui.activities.mealdetail.fragment.view

import android.support.annotation.CheckResult
import android.view.View
import com.github.st1hy.countthemcalories.ui.activities.mealdetail.view.MealDetailScreen
import com.github.st1hy.countthemcalories.ui.inject.app.PerFragment
import com.github.st1hy.countthemcalories.ui.inject.quantifier.view.FragmentRootView
import com.jakewharton.rxbinding.view.RxView
import kotlinx.android.synthetic.main.meal_detail_content.view.*
import rx.Observable
import javax.inject.Inject


internal interface MealDetailView : MealDetailScreen {
    @CheckResult fun getEditObservable(): Observable<Void>
    @CheckResult fun getDeleteObservable(): Observable<Void>
    @CheckResult fun getCopyObservable(): Observable<Void>
    fun setName(name: String)
    fun setDate(date: String)
    fun setEnergy(energy: String)
}

@PerFragment
internal class MealDetailViewImpl @Inject constructor(
        @FragmentRootView private val rootView: View,
        private val screen: MealDetailScreen
) : MealDetailView, MealDetailScreen by screen {

    override fun setName(name: String) {
        rootView.meal_detail_name.text = name
    }

    override fun getEditObservable() = RxView.clicks(rootView.meal_detail_edit)

    override fun setDate(date: String) {
        rootView.meal_detail_date.text = date
    }

    override fun setEnergy(energy: String) {
        rootView.meal_detail_energy_count.text = energy
    }

    override fun getDeleteObservable() = RxView.clicks(rootView.meal_detail_remove)

    override fun getCopyObservable() = RxView.clicks(rootView.meal_detail_copy)

}
