package com.github.st1hy.countthemcalories.ui.activities.addmeal.view

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.support.annotation.CheckResult
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityOptionsCompat
import com.github.st1hy.countthemcalories.ui.activities.addmeal.AddMealActivity
import com.github.st1hy.countthemcalories.ui.activities.addmeal.fragment.model.IngredientAction
import com.github.st1hy.countthemcalories.ui.activities.addmeal.model.EditIngredientResult
import com.github.st1hy.countthemcalories.ui.activities.addmeal.model.ShowIngredientsInfo
import com.github.st1hy.countthemcalories.ui.activities.ingredientdetail.IngredientDetailActivity
import com.github.st1hy.countthemcalories.ui.activities.ingredients.IngredientsActivity
import com.github.st1hy.countthemcalories.ui.activities.overview.OverviewActivity
import com.github.st1hy.countthemcalories.ui.contract.Ingredient
import com.github.st1hy.countthemcalories.ui.contract.IngredientFactory
import com.github.st1hy.countthemcalories.ui.contract.IngredientTemplate
import com.github.st1hy.countthemcalories.ui.contract.Meal
import com.github.st1hy.countthemcalories.ui.core.activityresult.ActivityLauncher
import com.github.st1hy.countthemcalories.ui.core.activityresult.ActivityResult
import com.github.st1hy.countthemcalories.ui.core.activityresult.StartParams
import com.github.st1hy.countthemcalories.ui.core.rx.Functions
import com.github.st1hy.countthemcalories.ui.inject.app.PerActivity
import com.jakewharton.rxbinding.view.RxView
import kotlinx.android.synthetic.main.add_meal_activity.*
import org.parceler.Parcels
import rx.Observable
import javax.inject.Inject


interface AddMealScreen {

    @CheckResult
    fun getAddIngredientButtonObservable(): Observable<Void>

    fun showSnackbarError(error: String)

    fun hideSnackbarError()

    fun onMealSaved(meal: Meal)

    @CheckResult
    fun showIngredientDetails(): Observable.Transformer<ShowIngredientsInfo, IngredientAction>

    @CheckResult
    fun newIngredients(): Observable.Transformer<Void, Ingredient>

}

@PerActivity
class AddMealScreenImpl @Inject constructor(
        private val activity: Activity,
        private val activityLauncher: ActivityLauncher
) : AddMealScreen {

    companion object {
        private const val REQUEST_PICK_INGREDIENT = 0x3903
        private const val REQUEST_EDIT_INGREDIENT = 0x3904
    }

    internal var addIngredientFab: FloatingActionButton = activity.add_meal_fab_add_ingredient
    private var ingredientsError: Snackbar? = null
    @Inject lateinit var factory: IngredientFactory

    override fun getAddIngredientButtonObservable(): Observable<Void> {
        return RxView.clicks(addIngredientFab)
    }

    override fun onMealSaved(meal: Meal) {
        activity.setResult(RESULT_OK)
        val intent = Intent(activity, OverviewActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra(OverviewActivity.EXTRA_JUMP_TO_DATE, meal.creationDate)
        activity.startActivity(intent)
    }

    override fun newIngredients() = Observable.Transformer<Void, Ingredient> { void ->
        void.map { getNewIngredientParams() }
                .compose(activityLauncher.startActivityForResult(REQUEST_PICK_INGREDIENT))
                .filter(ActivityResult.IS_OK)
                .map(this::getExtraIngredient)
                .filter(Functions.NOT_NULL)
                .map { template -> factory.newIngredient(template!!) }
    }

    private fun getNewIngredientParams(): StartParams {
        val intent = Intent(activity, IngredientsActivity::class.java)
        intent.action = IngredientsActivity.ACTION_SELECT_INGREDIENT
        return StartParams.of(intent, REQUEST_PICK_INGREDIENT)
    }

    private fun getExtraIngredient(result: ActivityResult): IngredientTemplate? {
        val data = result.data ?: return null
        return Parcels.unwrap(data.getParcelableExtra(AddMealActivity.EXTRA_INGREDIENT_PARCEL))
    }

    override fun showIngredientDetails() = InfoToAction { infoObservable ->
        infoObservable.map(this::getShowIngredientParams)
                .compose(activityLauncher.startActivityForResult(REQUEST_EDIT_INGREDIENT))
                .map(this::getIngredientAction)
    }

    private fun getIngredientAction(activityResult: ActivityResult): IngredientAction {
        val result = EditIngredientResult.fromIngredientDetailResult(activityResult.resultCode)
        val data = activityResult.data ?: return IngredientAction.CANCELED
        val requestId = data.getLongExtra(IngredientDetailActivity.EXTRA_INGREDIENT_ID_LONG, -2L)
        if (requestId == -2L) return IngredientAction.CANCELED
        return when (result) {
            EditIngredientResult.REMOVE -> IngredientAction.valueOf(IngredientAction.Type.REMOVE,
                    requestId, null)
            EditIngredientResult.EDIT -> {
                val ingredient = Parcels.unwrap<Ingredient>(data.getParcelableExtra<Parcelable>(
                        IngredientDetailActivity.EXTRA_INGREDIENT))
                        ?: return IngredientAction.CANCELED
                IngredientAction.valueOf(IngredientAction.Type.EDIT, requestId, ingredient)
            }
            EditIngredientResult.UNKNOWN -> IngredientAction.CANCELED
        }
    }

    private fun getShowIngredientParams(info: ShowIngredientsInfo): StartParams {
        var startOptions: Bundle? = null
        val sharedElements = info.sharedElements
        if (!sharedElements.isEmpty()) {
            val pairs = sharedElements.toTypedArray()
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    activity, *pairs)
            startOptions = options.toBundle()
        }
        val intent = Intent(activity, IngredientDetailActivity::class.java)
        intent.putExtra(IngredientDetailActivity.EXTRA_INGREDIENT_ID_LONG, info.id)
        intent.putExtra(IngredientDetailActivity.EXTRA_INGREDIENT,
                Parcels.wrap<Ingredient>(info.ingredient))
        return StartParams.of(intent, REQUEST_EDIT_INGREDIENT, startOptions)
    }

    override fun showSnackbarError(error: String) {
        if (!isSnackbarShown()) {
            ingredientsError = Snackbar.make(addIngredientFab, error, Snackbar.LENGTH_INDEFINITE).apply {
                addCallback(object : Snackbar.Callback() {
                    override fun onDismissed(snackbar: Snackbar?, event: Int) {
                        super.onDismissed(snackbar, event)
                        this@AddMealScreenImpl.ingredientsError = null
                    }
                })
                show()
            }
        }
    }

    override fun hideSnackbarError() {
        if (isSnackbarShown() && ingredientsError != null) ingredientsError!!.dismiss()
    }

    private fun isSnackbarShown(): Boolean = ingredientsError?.isShownOrQueued ?: false
}

private typealias InfoToAction = Observable.Transformer<ShowIngredientsInfo, IngredientAction>
