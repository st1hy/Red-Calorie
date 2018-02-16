package com.github.st1hy.countthemcalories.ui.activities.tags.adapter.holder

import android.content.res.ColorStateList
import android.support.annotation.ColorInt
import android.support.v4.widget.CompoundButtonCompat
import android.support.v7.widget.AppCompatCheckBox
import android.view.View
import android.view.ViewGroup
import com.github.st1hy.countthemcalories.ui.activities.tags.model.ClickEvent
import com.github.st1hy.countthemcalories.ui.activities.tags.model.TagsViewModel
import com.github.st1hy.countthemcalories.ui.activities.tags.model.Type
import com.github.st1hy.countthemcalories.ui.contract.Tag
import com.github.st1hy.countthemcalories.ui.contract.TagFactory
import com.github.st1hy.countthemcalories.ui.core.adapter.PositionDelegate
import com.github.st1hy.countthemcalories.ui.core.rx.Transformers
import com.github.st1hy.countthemcalories.ui.core.viewcontrol.ScrollingItemDelegate
import com.github.st1hy.countthemcalories.ui.inject.tags.TagRootView
import com.jakewharton.rxbinding.widget.RxCompoundButton
import kotlinx.android.synthetic.main.tags_item_inner.view.*
import kotlinx.android.synthetic.main.tags_item_scrolling.view.*
import rx.subjects.PublishSubject
import javax.inject.Inject


class TagItemHolder @Inject constructor(
        @TagRootView itemView: View,
        private val viewModel: TagsViewModel
) : TagViewHolder(itemView) {

    private val checkBox: AppCompatCheckBox = itemView.tags_item_checkbox

    val reusableTag: Tag by lazy { tagFactory.newTag() }
    private var isEnabled: Boolean = false

    @Inject lateinit var stateChanges: PublishSubject<TagViewHolder>
    @Inject lateinit var clickEvents: PublishSubject<ClickEvent>
    @Inject lateinit var position: PositionDelegate
    @Inject lateinit var tagFactory: TagFactory

    private val scrollingItemDelegate: ScrollingItemDelegate = ScrollingItemDelegate.Builder.create()
            .setScrollView(itemView.tags_item_scrollview!!)
            .setLeft(itemView.tags_item_delete_frame!!)
            .setCenter(itemView.tag_item_button!!)
            .setRight(itemView.tags_item_edit_frame!!)
            .build()

    var isChecked: Boolean
        get() {
            return checkBox.isChecked
        }
        set(isSelected) {
            checkBox.isChecked = isSelected
        }


    init {
        itemView.tag_item_button.setOnClickListener { onEvent(Type.OPEN) }
        itemView.tags_item_delete.setOnClickListener { onEvent(Type.REMOVE) }
        itemView.tags_item_edit.setOnClickListener { onEvent(Type.EDIT) }
    }

    private fun onEvent(type: Type) {
        if (isEnabled) clickEvents.onNext(ClickEvent(type, position.get(), this))
    }

    fun bind(position: Int, tag: Tag) {
        this.position.set(position)
        itemView.tags_item_name.text = tag.displayName
        itemView.tags_item_count.text = viewModel.getCounterFor(tag.ingredientCount)
    }

    override fun onAttached() {
        position.onAttached()
        scrollingItemDelegate.onAttached()
        scrollingItemDelegate.subscribe(
                RxCompoundButton.checkedChanges(checkBox)
                        .map { this }
                        .compose(Transformers.channel(stateChanges))
                        .subscribe()
        )
    }

    override fun onDetached() {
        position.onDetached()
        scrollingItemDelegate.onDetached()
    }

    fun fillParent(parent: ViewGroup) {
        scrollingItemDelegate.fillParent(parent)
    }

    fun setSelectable(inSelectMode: Boolean) {
        checkBox.isEnabled = inSelectMode
    }

    fun setCheckedTint(@ColorInt color: Int) {
        CompoundButtonCompat.setButtonTintList(checkBox, ColorStateList.valueOf(color))
    }

    fun setEnabled(isEnabled: Boolean) {
        this.isEnabled = isEnabled
    }
}
