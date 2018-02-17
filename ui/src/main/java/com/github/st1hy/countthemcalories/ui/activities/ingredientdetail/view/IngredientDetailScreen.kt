package com.github.st1hy.countthemcalories.ui.activities.ingredientdetail.view

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.support.v4.app.ActivityCompat
import com.github.st1hy.countthemcalories.ui.activities.ingredientdetail.IngredientDetailActivity
import com.github.st1hy.countthemcalories.ui.contract.Ingredient
import com.github.st1hy.countthemcalories.ui.inject.app.PerActivity
import kotlinx.android.synthetic.main.ingredient_detail_activity.*
import org.parceler.Parcels
import javax.inject.Inject

internal interface IngredientDetailScreen {
    fun finishEdit(ingredientId: Long, ingredient: Ingredient)
    fun finishRemove(ingredientId: Long)
}

@PerActivity internal class IngredientDetailScreenImpl @Inject constructor(
        private val activity: Activity
) : IngredientDetailScreen {

    init {
        activity.ingredient_detail_root.setOnClickListener { finishActivity() }
    }

    override fun finishEdit(ingredientId: Long,
                            ingredient: Ingredient) {
        val intent = Intent()
        intent.putExtra(IngredientDetailActivity.EXTRA_INGREDIENT_ID_LONG, ingredientId)
        intent.putExtra(IngredientDetailActivity.EXTRA_INGREDIENT, Parcels.wrap(ingredient))
        activity.setResult(RESULT_OK, intent)
        finishActivity()
    }

    override fun finishRemove(ingredientId: Long) {
        val intent = Intent()
        intent.putExtra(IngredientDetailActivity.EXTRA_INGREDIENT_ID_LONG, ingredientId)
        activity.setResult(IngredientDetailActivity.RESULT_REMOVE, intent)
        finishActivity()
    }

    private fun finishActivity() {
        ActivityCompat.finishAfterTransition(activity)
    }
}
