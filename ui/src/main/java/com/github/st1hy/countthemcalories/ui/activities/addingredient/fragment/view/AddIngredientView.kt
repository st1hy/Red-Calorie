package com.github.st1hy.countthemcalories.ui.activities.addingredient.fragment.view

import android.content.res.Resources
import android.support.annotation.CheckResult
import android.support.annotation.StringRes
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.github.st1hy.countthemcalories.ui.activities.addingredient.fragment.model.InputType
import com.github.st1hy.countthemcalories.ui.activities.addingredient.view.AddIngredientScreen
import com.github.st1hy.countthemcalories.ui.core.state.Visibility
import com.github.st1hy.countthemcalories.ui.inject.app.PerFragment
import com.github.st1hy.countthemcalories.ui.inject.quantifier.view.FragmentRootView
import com.google.common.collect.ImmutableMap
import com.jakewharton.rxbinding.view.RxView
import com.jakewharton.rxbinding.widget.RxTextView
import kotlinx.android.synthetic.main.add_ingredient_content.view.*
import rx.Observable
import javax.inject.Inject

internal interface AddIngredientView : AddIngredientScreen {
    @CheckResult fun getNameObservable(): Observable<CharSequence>
    @CheckResult fun getValueObservable(): Observable<CharSequence>
    @CheckResult fun getSearchObservable(): Observable<Void>
    @CheckResult fun getSelectTypeObservable(): Observable<Void>
    fun setSelectedUnitName(unitName: String)
    fun setName(name: String)
    fun setEnergyDensityValue(energyValue: String)
    fun showError(type: InputType, @StringRes errorResId: Int)
    fun hideError(type: InputType)
    fun requestFocusTo(type: InputType)
    fun setNoCategoriesVisibility(visibility: Visibility)
}


@PerFragment internal class AddIngredientViewController @Inject constructor(
        @FragmentRootView private val rootView: View,
        private val resources: Resources,
        private val screen: AddIngredientScreen
) : AddIngredientScreen by screen, AddIngredientView {

    private var name: EditText = rootView.add_ingredient_name
    private var energyDensityValue: EditText = rootView.add_ingredient_energy_density
    private var energyDensityUnit: Button = rootView.add_ingredient_unit

    private val inputMap: Map<InputType, EditText> = ImmutableMap.of(
            InputType.NAME, name,
            InputType.VALUE, energyDensityValue
    )

    override fun setSelectedUnitName(unitName: String) {
        energyDensityUnit.text = unitName
    }

    override fun setName(name: String) {
        this.name.setText(name)
        this.name.setSelection(name.length)
    }

    override fun setEnergyDensityValue(energyValue: String) {
        this.energyDensityValue.setText(energyValue)
        this.energyDensityValue.setSelection(energyValue.length)
    }

    override fun getNameObservable(): Observable<CharSequence> {
        return RxTextView.textChanges(name).skip(1)
    }

    override fun getValueObservable(): Observable<CharSequence> {
        return RxTextView.textChanges(energyDensityValue).skip(1)
    }

    override fun showError(type: InputType, @StringRes errorResId: Int) {
        getEditTextOf(type)?.error = resources.getString(errorResId)
    }

    override fun hideError(type: InputType) {
        getEditTextOf(type)?.error = null
    }

    override fun requestFocusTo(type: InputType) {
        getEditTextOf(type)?.requestFocus()
    }

    override fun getSearchObservable(): Observable<Void> = RxView.clicks(
            rootView.add_ingredient_name_search)

    override fun getSelectTypeObservable(): Observable<Void> = RxView.clicks(energyDensityUnit)

    override fun setNoCategoriesVisibility(visibility: Visibility) {
        rootView.add_ingredient_categories_empty.visibility = visibility.visibility
    }

    private fun getEditTextOf(type: InputType): EditText? {
        return inputMap[type]
    }
}
