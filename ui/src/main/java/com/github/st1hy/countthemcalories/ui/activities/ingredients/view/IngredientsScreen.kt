package com.github.st1hy.countthemcalories.ui.activities.ingredients.view

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Parcelable
import android.support.annotation.CheckResult
import com.github.st1hy.countthemcalories.ui.activities.addingredient.AddIngredientActivity
import com.github.st1hy.countthemcalories.ui.activities.addingredient.EditIngredientActivity
import com.github.st1hy.countthemcalories.ui.activities.addmeal.AddMealActivity
import com.github.st1hy.countthemcalories.ui.activities.ingredients.IngredientsActivity.REQUEST_ADD_INGREDIENT
import com.github.st1hy.countthemcalories.ui.activities.ingredients.IngredientsActivity.REQUEST_EDIT
import com.github.st1hy.countthemcalories.ui.activities.ingredients.model.AddIngredientParams
import com.github.st1hy.countthemcalories.ui.contract.IngredientTemplate
import com.github.st1hy.countthemcalories.ui.core.activityresult.ActivityLauncher
import com.github.st1hy.countthemcalories.ui.core.activityresult.ActivityResult
import com.github.st1hy.countthemcalories.ui.core.activityresult.StartParams
import com.github.st1hy.countthemcalories.ui.core.rx.Functions
import com.github.st1hy.countthemcalories.ui.inject.app.PerActivity
import com.jakewharton.rxbinding.view.RxView
import kotlinx.android.synthetic.main.ingredients_app_bar.*
import org.parceler.Parcels
import rx.Observable
import javax.inject.Inject

internal interface IngredientsScreen {
    @CheckResult
    fun getOnAddIngredientClickedObservable(): Observable<Void>
    @CheckResult
    fun addNewIngredient(): Observable.Transformer<AddIngredientParams, IngredientTemplate>
    fun editIngredientTemplate(requestID: Long, ingredientTemplate: IngredientTemplate)
    fun onIngredientSelected(ingredientTemplate: IngredientTemplate)
    fun addToNewMeal(ingredientTemplate: IngredientTemplate)
}

private typealias ParamsToTemplace = Observable.Transformer<AddIngredientParams, IngredientTemplate>

@PerActivity internal class IngredientsScreenImpl @Inject constructor(
        private val activity: Activity,
        private val activityLauncher: ActivityLauncher
) : IngredientsScreen {


    @CheckResult
    override fun addNewIngredient() = ParamsToTemplace { paramsObservable ->
        paramsObservable
                .map(this::getAddIngredientParams)
                .compose(activityLauncher.startActivityForResult(REQUEST_ADD_INGREDIENT))
                .filter(ActivityResult.IS_OK)
                .map(this::resultToTemplate)
                .filter(Functions.NOT_NULL)
    }

    private fun resultToTemplate(activityResult: ActivityResult): IngredientTemplate? {
        val data = activityResult.data ?: return null
        return Parcels.unwrap<IngredientTemplate>(data.getParcelableExtra<Parcelable>(
                AddIngredientActivity.RESULT_INGREDIENT_TEMPLATE))
    }

    private fun getAddIngredientParams(addIngredientParams: AddIngredientParams): StartParams {
        val type = addIngredientParams.type
        val intent = Intent(activity, AddIngredientActivity::class.java)
        intent.action = type.action
        intent.putExtra(AddIngredientActivity.ARG_EXTRA_NAME,
                addIngredientParams.extraName)
        return StartParams.of(intent, REQUEST_ADD_INGREDIENT)
    }

    override fun getOnAddIngredientClickedObservable(): Observable<Void> {
        return RxView.clicks(activity.ingredients_fab)
    }

    override fun onIngredientSelected(ingredientTemplate: IngredientTemplate) {
        val result = Intent()
        result.putExtra(AddMealActivity.EXTRA_INGREDIENT_PARCEL, Parcels.wrap(ingredientTemplate))
        activity.setResult(RESULT_OK, result)
        activity.finish()
    }

    override fun addToNewMeal(ingredientTemplate: IngredientTemplate) {
        val intent = Intent(activity, AddMealActivity::class.java)
        intent.putExtra(AddMealActivity.EXTRA_INGREDIENT_PARCEL, Parcels.wrap(ingredientTemplate))
        activity.startActivity(intent)
    }


    override fun editIngredientTemplate(requestID: Long, ingredientTemplate: IngredientTemplate) {
        val intent = Intent(activity, EditIngredientActivity::class.java)
        intent.putExtra(AddIngredientActivity.ARG_EDIT_REQUEST_ID_LONG, requestID)
        intent.putExtra(AddIngredientActivity.ARG_EDIT_INGREDIENT_PARCEL,
                Parcels.wrap(ingredientTemplate))
        activity.startActivityForResult(intent, REQUEST_EDIT)
    }

}
