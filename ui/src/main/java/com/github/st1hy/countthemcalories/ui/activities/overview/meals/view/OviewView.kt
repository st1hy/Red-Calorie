package com.github.st1hy.countthemcalories.ui.activities.overview.meals.view

import android.support.annotation.DrawableRes
import android.view.View
import com.github.st1hy.countthemcalories.ui.activities.overview.model.MealDetailAction
import com.github.st1hy.countthemcalories.ui.activities.overview.model.MealDetailParams
import com.github.st1hy.countthemcalories.ui.activities.overview.view.OverviewScreen
import com.github.st1hy.countthemcalories.ui.core.state.Visibility
import com.github.st1hy.countthemcalories.ui.inject.app.PerFragment
import com.github.st1hy.countthemcalories.ui.inject.quantifier.view.FragmentRootView
import kotlinx.android.synthetic.main.overview_list.view.*
import rx.Observable
import javax.inject.Inject


internal interface OverviewView : OverviewScreen {

    fun setEmptyListVisibility(visibility: Visibility)

    fun setEmptyBackground(@DrawableRes drawableResId: Int)
}

private typealias ParamsToAction = Observable.Transformer<MealDetailParams, MealDetailAction>

@PerFragment internal class OverviewViewImpl @Inject constructor(
        private val screen: OverviewScreen,
        @FragmentRootView private val rootView: View
) : OverviewScreen by screen, OverviewView {

    override fun setEmptyListVisibility(visibility: Visibility) {
        rootView.overview_empty_text.visibility = visibility.visibility
    }

    override fun setEmptyBackground(@DrawableRes drawableResId: Int) {
        rootView.overview_empty_background.setImageResource(drawableResId)
    }

}
