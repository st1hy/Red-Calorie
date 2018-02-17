package com.github.st1hy.countthemcalories.ui.activities.mealdetail.view

import android.app.Activity
import android.content.Intent
import android.support.v4.app.ActivityCompat
import com.github.st1hy.countthemcalories.ui.activities.mealdetail.MealDetailActivity
import com.github.st1hy.countthemcalories.ui.inject.app.PerActivity
import kotlinx.android.synthetic.main.meal_detail_activity.*
import javax.inject.Inject

internal interface MealDetailScreen {
    fun editMealWithId(mealId: Long)
    fun deleteMealWithId(mealId: Long)
    fun copyMealWithId(mealId: Long)
}


@PerActivity internal class MealDetailScreenImpl @Inject constructor(
        private val activity: Activity
) : MealDetailScreen {

    init {
        activity.meal_detail_root.setOnClickListener {
            ActivityCompat.finishAfterTransition(activity)
        }
    }

    override fun editMealWithId(mealId: Long) {
        setResultAndFinish(MealDetailActivity.RESULT_EDIT, mealId)
    }

    override fun deleteMealWithId(mealId: Long) {
        setResultAndFinish(MealDetailActivity.RESULT_DELETE, mealId)
    }

    override fun copyMealWithId(mealId: Long) {
        setResultAndFinish(MealDetailActivity.RESULT_COPY, mealId)
    }

    private fun setResultAndFinish(resultCode: Int, mealId: Long) {
        val intent = Intent()
        intent.putExtra(MealDetailActivity.EXTRA_RESULT_MEAL_ID_LONG, mealId)
        activity.setResult(resultCode, intent)
        ActivityCompat.finishAfterTransition(activity)
    }
}
