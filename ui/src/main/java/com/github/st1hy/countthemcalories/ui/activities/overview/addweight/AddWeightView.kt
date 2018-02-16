package com.github.st1hy.countthemcalories.ui.activities.overview.addweight

import android.app.Activity
import android.support.annotation.CheckResult
import com.github.st1hy.countthemcalories.ui.R
import com.github.st1hy.countthemcalories.ui.activities.settings.model.SettingsModel
import com.github.st1hy.countthemcalories.ui.core.dialog.CustomDialogViewEditTextController
import com.github.st1hy.countthemcalories.ui.core.rx.RxAlertDialog
import com.github.st1hy.countthemcalories.ui.inject.app.PerActivity
import com.google.common.base.Strings
import com.jakewharton.rxbinding.view.RxView
import kotlinx.android.synthetic.main.add_weight_dialog_custom_view.view.*
import kotlinx.android.synthetic.main.overview_fab.*
import rx.Observable
import javax.inject.Inject


@PerActivity internal class AddWeightView @Inject constructor(private val activity: Activity) {

    @Inject lateinit var settingsModel: SettingsModel


    @CheckResult
    fun addWeightButton(): Observable<Void> {
        return RxView.clicks(activity.overview_fab_add_weight)
    }

    @CheckResult
    fun openAddWeightDialog(initialValue: String): Observable<Float> {
        val rxAlertDialog = RxAlertDialog.Builder.with(activity)
                .title(R.string.add_weight_dialog_title)
                .customView(R.layout.add_weight_dialog_custom_view)
                .positiveButton(android.R.string.ok)
                .negativeButton(android.R.string.cancel)
                .show()
        val unitView = rxAlertDialog.customView!!.add_weight_unit
        unitView.text = activity.getString(settingsModel.bodyMassUnit.nameRes)
        return CustomDialogViewEditTextController.editTextValueOnOk(
                rxAlertDialog, initialValue, R.id.add_weight_value)
                .filter { s -> !Strings.isNullOrEmpty(s) }
                .map { string -> string.toFloat() }
    }
}