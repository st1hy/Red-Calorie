package com.github.st1hy.countthemcalories.ui.activities.overview.meals.adapter.holder

import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.github.st1hy.countthemcalories.ui.activities.overview.meals.adapter.MealInteraction
import com.github.st1hy.countthemcalories.ui.activities.overview.meals.adapter.inject.MealItemRootView
import com.github.st1hy.countthemcalories.ui.activities.overview.meals.adapter.inject.PerMealRow
import com.github.st1hy.countthemcalories.ui.contract.Meal
import com.github.st1hy.countthemcalories.ui.core.headerpicture.imageholder.ImageHolderDelegate
import com.github.st1hy.countthemcalories.ui.core.rx.Transformers
import com.github.st1hy.countthemcalories.ui.core.viewcontrol.ScrollingItemDelegate
import com.jakewharton.rxbinding.view.RxView
import kotlinx.android.synthetic.main.overview_item.view.*
import kotlinx.android.synthetic.main.overview_item_scrolling.view.*
import rx.subjects.PublishSubject
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject


@PerMealRow
class MealItemHolder @Inject constructor(@MealItemRootView itemView: View
) : AbstractMealItemHolder(itemView) {

    @Inject lateinit var imageHolderDelegate: ImageHolderDelegate

    val image: ImageView = itemView.overview_item_image

    private val scrollingItemDelegate: ScrollingItemDelegate = ScrollingItemDelegate.Builder.create()
            .setLeft(itemView.overview_item_delete_frame)
            .setCenter(itemView.overview_item_content)
            .setRight(itemView.overview_item_edit_frame)
            .setScrollView(itemView.overview_item_scrollview)
            .build()

    var meal: Meal? = null
    private val subscriptions = CompositeSubscription()

    fun fillParent(parent: ViewGroup) {
        scrollingItemDelegate.fillParent(parent)
    }

    fun setName(name: String) {
        itemView.overview_item_name.text = name
    }

    fun setTotalEnergy(totalEnergy: String) {
        itemView.overview_item_energy.text = totalEnergy
    }

    fun setTotalEnergyUnit(unit: String) {
        itemView.overview_item_energy_unit.text = unit
    }

    fun setIngredients(ingredients: String) {
        itemView.overview_item_ingredients.text = ingredients
    }

    fun setDate(date: String) {
        itemView.overview_item_date.text = date
    }

    fun onAttached(subject: PublishSubject<MealInteraction>) {
        scrollingItemDelegate.onAttached()
        imageHolderDelegate.onAttached()
        subscriptions.add(
                RxView.clicks(itemView.overview_item_button).map { MealInteraction.Type.OPEN }
                        .mergeWith(RxView.clicks(
                                itemView.overview_item_edit).map { MealInteraction.Type.EDIT })
                        .mergeWith(RxView.clicks(
                                itemView.overview_item_delete).map { MealInteraction.Type.DELETE })
                        .map { type -> MealInteraction.of(type, this) }
                        .compose(Transformers.channel(subject))
                        .subscribe()
        )
    }

    fun onDetached() {
        scrollingItemDelegate.onDetached()
        imageHolderDelegate.onDetached()
        subscriptions.clear()
    }

    fun setEnabled(enabled: Boolean) {
        itemView.overview_item_button.isEnabled = enabled
        itemView.overview_item_edit.isEnabled = enabled
        itemView.overview_item_delete.isEnabled = enabled
    }

    fun setImageUri(uri: Uri) {
        imageHolderDelegate.displayImage(uri)
    }

}
