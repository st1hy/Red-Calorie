package com.github.st1hy.countthemcalories.ui.activities.mealdetail.fragment.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.meal_detail_ingredient_item.view.*

internal class IngredientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun setName(name: String) {
        itemView.meal_detail_ingredient_item_name.text = name
    }

    fun setEnergy(energyCount: String) {
        itemView.meal_detail_ingredient_item_energy.text = energyCount
    }

    fun setAmount(amount: String) {
        itemView.meal_detail_ingredient_item_amount.text = amount
    }
}