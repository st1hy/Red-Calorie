package com.github.st1hy.countthemcalories.ui.activities.ingredients.fragment.model

import com.github.st1hy.countthemcalories.ui.activities.ingredients.fragment.adapter.viewholder.IngredientViewHolder


internal class IngredientClick(val type: Type, val holder: IngredientViewHolder) {

    enum class Type {
        DELETE, OPEN, EDIT
    }
}