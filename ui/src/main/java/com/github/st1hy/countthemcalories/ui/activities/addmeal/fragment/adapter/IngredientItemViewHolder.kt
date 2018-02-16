package com.github.st1hy.countthemcalories.ui.activities.addmeal.fragment.adapter

import android.net.Uri
import android.support.annotation.CheckResult
import android.support.annotation.DrawableRes
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.github.st1hy.countthemcalories.ui.activities.addmeal.fragment.adapter.inject.IngredientImage
import com.github.st1hy.countthemcalories.ui.activities.addmeal.fragment.adapter.inject.IngredientImageHolder
import com.github.st1hy.countthemcalories.ui.activities.addmeal.fragment.adapter.inject.IngredientRootView
import com.github.st1hy.countthemcalories.ui.activities.addmeal.fragment.adapter.inject.PerIngredientRow
import com.github.st1hy.countthemcalories.ui.contract.Ingredient
import com.github.st1hy.countthemcalories.ui.core.headerpicture.imageholder.ImageHolderDelegate
import com.github.st1hy.countthemcalories.ui.core.rx.Functions
import com.github.st1hy.countthemcalories.ui.core.rx.Transformers
import com.jakewharton.rxbinding.view.RxView
import kotlinx.android.synthetic.main.add_meal_ingredient_item.view.*
import rx.Observable
import rx.subjects.PublishSubject
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject


@PerIngredientRow
class IngredientItemViewHolder @Inject constructor(
        @IngredientRootView itemView: View
) : RecyclerView.ViewHolder(itemView) {

    @IngredientImageHolder lateinit var imageHolderDelegate: ImageHolderDelegate
    @IngredientImage lateinit var image: ImageView

    private val name: TextView = itemView.add_meal_ingredient_item_name
    private val energyDensity: TextView = itemView.add_meal_ingredient_energy_density
    private val amount: TextView = itemView.add_meal_ingredient_amount
    private val calorieCount: TextView = itemView.add_meal_ingredient_calorie_count
    private val calorieUnit: TextView = itemView.add_meal_ingredient_calorie_unit
    private val compatView: ViewGroup = itemView.add_meal_ingredient_compact

    @Inject lateinit var ingredientClicks: PublishSubject<IngredientItemViewHolder>

    var ingredient: Ingredient? = null
    
    private val subscriptions = CompositeSubscription()

    fun setName(name: String) {
        this.name.text = name
    }

    fun setAmount(amount: String) {
        this.amount.text = amount
    }

    fun setCalorieCount(calorieCount: String) {
        this.calorieCount.text = calorieCount
    }

    fun setCalorieUnit(unit: String) {
        this.calorieUnit.text = unit
    }

    fun setEnergyDensity(energyDensity: String) {
        this.energyDensity.text = energyDensity
    }

    @CheckResult
    fun clicks(): Observable<IngredientItemViewHolder> {
        return RxView.clicks(compatView).map(Functions.into(this))
    }

    fun setImageUri(uri: Uri) {
        imageHolderDelegate.displayImage(uri)
    }

    fun setImagePlaceholder(@DrawableRes placeholderResId: Int) {
        imageHolderDelegate.setImagePlaceholder(placeholderResId)
    }

    fun onAttached() {
        imageHolderDelegate.onAttached()
        subscriptions.add(
                clicks().compose(Transformers.channel(ingredientClicks))
                        .subscribe()
        )
    }

    fun onDetached() {
        imageHolderDelegate.onDetached()
        subscriptions.clear()
    }

    fun setEnabled(enabled: Boolean) {
        compatView.isEnabled = enabled
    }

}
