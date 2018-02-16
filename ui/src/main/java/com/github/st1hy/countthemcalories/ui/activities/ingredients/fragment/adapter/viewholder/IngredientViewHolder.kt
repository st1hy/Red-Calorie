package com.github.st1hy.countthemcalories.ui.activities.ingredients.fragment.adapter.viewholder

import android.net.Uri
import android.support.annotation.DrawableRes
import android.view.View
import android.view.ViewGroup
import com.github.st1hy.countthemcalories.ui.activities.addmeal.fragment.adapter.inject.IngredientRootView
import com.github.st1hy.countthemcalories.ui.activities.ingredients.fragment.model.IngredientClick
import com.github.st1hy.countthemcalories.ui.contract.IngredientTemplate
import com.github.st1hy.countthemcalories.ui.contract.IngredientTemplateFactory
import com.github.st1hy.countthemcalories.ui.core.adapter.PositionDelegate
import com.github.st1hy.countthemcalories.ui.core.headerpicture.imageholder.ImageHolderDelegate
import com.github.st1hy.countthemcalories.ui.core.viewcontrol.ScrollingItemDelegate
import kotlinx.android.synthetic.main.ingredients_item.view.*
import kotlinx.android.synthetic.main.ingredients_item_scrolling.view.*
import rx.subjects.PublishSubject
import javax.inject.Inject

internal class IngredientViewHolder @Inject constructor(
        @IngredientRootView itemView: View
) : AbstractIngredientsViewHolder(itemView) {

    @Inject lateinit var clicks: PublishSubject<IngredientClick>
    @Inject lateinit var factory: IngredientTemplateFactory
    @Inject lateinit var imageHolderDelegate: ImageHolderDelegate
    @Inject lateinit var position: PositionDelegate

    private val scrollingItemDelegate: ScrollingItemDelegate = ScrollingItemDelegate.Builder.create()
            .setLeft(itemView.ingredients_item_delete_frame)
            .setCenter(itemView.ingredients_item_content)
            .setRight(itemView.ingredients_item_edit_frame)
            .setScrollView(itemView.ingredients_item_scrollview)
            .build()

    val reusableIngredient: IngredientTemplate by lazy {
        factory.newIngredientTemplate()
        //        reusableIngredient.setTranslations(new I18n());
    }
    private var isEnabled = true

    fun getPositionInAdapter(): Int = position.get()

    init {
        itemView.ingredients_item_button.setOnClickListener {
            onClicked(IngredientClick.Type.OPEN)
        }
        itemView.ingredients_item_edit.setOnClickListener {
            onClicked(IngredientClick.Type.EDIT)
        }
        itemView.ingredients_item_delete.setOnClickListener {
            onClicked(IngredientClick.Type.DELETE)
        }
    }

    private fun onClicked(type: IngredientClick.Type) {
        if (isEnabled) clicks.onNext(IngredientClick(type, this))
    }

    fun setName(name: String) {
        itemView.ingredients_item_name.text = name
    }

    fun setEnergyDensity(value: String) {
        itemView.ingredients_item_energy_density.text = value
    }

    fun setUnit(value: String) {
        itemView.ingredients_item_energy_density_unit.text = value
    }
    fun fillParent(parent: ViewGroup) {
        scrollingItemDelegate.fillParent(parent)
    }

    override fun onAttached() {
        scrollingItemDelegate.onAttached()
        position.onAttached()
        imageHolderDelegate.onAttached()
    }

    override fun onDetached() {
        scrollingItemDelegate.onDetached()
        position.onDetached()
        imageHolderDelegate.onDetached()
    }

    fun setEnabled(enabled: Boolean) {
        isEnabled = enabled
    }

    fun setImageUri(uri: Uri) {
        imageHolderDelegate.displayImage(uri)
    }

    fun setImagePlaceholder(@DrawableRes drawableResId: Int) {
        imageHolderDelegate.setImagePlaceholder(drawableResId)
    }

    fun setPosition(position: Int) {
        this.position.set(position)
    }
}