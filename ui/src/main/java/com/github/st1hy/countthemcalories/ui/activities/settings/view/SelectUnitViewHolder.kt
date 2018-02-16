package com.github.st1hy.countthemcalories.ui.activities.settings.view

import android.support.annotation.StringRes
import android.view.View
import com.jakewharton.rxbinding.view.RxView
import kotlinx.android.synthetic.main.settings_select_unit.view.*
import rx.Observable

internal class SelectUnitViewHolder(private val root: View) {

    fun setTitle(@StringRes titleRes: Int) {
        root.settings_unit_item_title.setText(titleRes)
    }

    fun setUnit(unit: String) {
        this.root.settings_unit_item_current_unit.text = unit
    }

    fun clickObservable(): Observable<Void> {
        return RxView.clicks(root.settings_unit_item_root)
    }
}