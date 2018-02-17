package com.github.st1hy.countthemcalories.ui.activities.ingredientdetail.fragment.view

import android.support.annotation.CheckResult
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import com.github.st1hy.countthemcalories.ui.activities.ingredientdetail.view.IngredientDetailScreen
import com.github.st1hy.countthemcalories.ui.core.rx.Functions
import com.github.st1hy.countthemcalories.ui.inject.app.PerFragment
import com.github.st1hy.countthemcalories.ui.inject.quantifier.view.FragmentRootView
import com.jakewharton.rxbinding.view.RxView
import com.jakewharton.rxbinding.widget.RxTextView
import com.jakewharton.rxbinding.widget.TextViewEditorActionEvent
import kotlinx.android.synthetic.main.ingredient_detail_content.view.*
import rx.Observable
import javax.inject.Inject

internal interface IngredientDetailView : IngredientDetailScreen {
    @CheckResult fun getAmountObservable(): Observable<CharSequence>
    @CheckResult fun getRemoveObservable(): Observable<Void>
    @CheckResult fun getAcceptObservable(): Observable<Void>
    fun getImageView(): ImageView
    fun getCurrentAmount(): String
    fun setName(name: String)
    fun setEnergyDensity(readableEnergyDensity: String)
    fun setAmount(readableAmount: String)
    fun setCalorieCount(calorieCount: String)
    fun setAmountError(errorResId: String?)
    fun setUnitName(unitName: String)
    fun hideSoftKeyboard()
}

@PerFragment internal class IngredientDetailViewImpl @Inject constructor(
        @FragmentRootView private val rootView: View,
        screen: IngredientDetailScreen,
        private val inputMethodManager: InputMethodManager
) : IngredientDetailScreen by screen, IngredientDetailView {

    private var editAmount: EditText = rootView.add_meal_ingredient_edit_amount

    override fun setName(name: String) {
        rootView.add_meal_ingredient_item_name.text = name
    }

    override fun setEnergyDensity(readableEnergyDensity: String) {
        rootView.add_meal_ingredient_energy_density.text = readableEnergyDensity
    }

    override fun setAmount(readableAmount: String) {
        val text = this.editAmount.text
        text.clear()
        text.append(readableAmount)
    }

    override fun setCalorieCount(calorieCount: String) {
        rootView.add_meal_ingredient_calorie_count.text = calorieCount
    }

    override fun getAmountObservable() = RxTextView.textChanges(editAmount)

    override fun setAmountError(errorResId: String?) {
        editAmount.error = errorResId
    }

    override fun hideSoftKeyboard() {
        inputMethodManager.hideSoftInputFromWindow(editAmount.windowToken, 0)
    }

    override fun getImageView(): ImageView {
        return rootView.add_meal_ingredient_image as ImageView
    }

    override fun setUnitName(unitName: String) {
        rootView.add_meal_ingredient_unit.text = unitName
    }

    override fun getAcceptObservable(): Observable<Void> =
            RxTextView.editorActionEvents(editAmount)
                    .filter(this::enterPressed)
                    .map(Functions.INTO_VOID)
                    .mergeWith(RxView.clicks(rootView.add_meal_ingredient_accept))

    private fun enterPressed(event: TextViewEditorActionEvent): Boolean {
        return event.actionId() == EditorInfo.IME_ACTION_DONE
                || event.keyEvent()?.keyCode == KeyEvent.KEYCODE_ENTER
    }

    override fun getRemoveObservable(): Observable<Void> =
            RxView.clicks(rootView.add_meal_ingredient_remove)

    override fun getCurrentAmount() = editAmount.text.toString()
}
