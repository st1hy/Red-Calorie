package com.github.st1hy.countthemcalories.ui.activities.addingredient.fragment.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import com.github.st1hy.countthemcalories.ui.contract.Tag
import kotlinx.android.synthetic.main.add_ingredient_tag.view.*
import rx.functions.Action1


internal abstract class TagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

internal class ItemTagViewHolder(
        itemView: View,
        private val onRemove: Action1<Tag>
) : TagViewHolder(itemView) {

    private var tag: Tag? = null

    init {
        itemView.add_ingredient_category_remove.setOnClickListener {
            if (tag != null) onRemove.call(tag)
        }
    }

    fun setCategoryName(name: String) {
        itemView.add_ingredient_category_name.text = name
    }

    fun setTag(tag: Tag) {
        this.tag = tag
    }
}
