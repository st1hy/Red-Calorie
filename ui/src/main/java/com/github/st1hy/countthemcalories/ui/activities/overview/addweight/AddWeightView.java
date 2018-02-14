package com.github.st1hy.countthemcalories.ui.activities.overview.addweight;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.ui.R;
import com.github.st1hy.countthemcalories.ui.activities.settings.model.SettingsModel;
import com.github.st1hy.countthemcalories.ui.core.dialog.CustomDialogViewEditTextController;
import com.github.st1hy.countthemcalories.ui.core.rx.RxAlertDialog;
import com.github.st1hy.countthemcalories.ui.inject.app.PerActivity;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.jakewharton.rxbinding.view.RxView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

@PerActivity
public class AddWeightView {

    @BindView(R.id.overview_fab_add_weight)
    View addWeight;

    @Inject
    SettingsModel settingsModel;

    private final Context context;

    @Inject
    public AddWeightView(@NonNull Activity activity) {
        this.context = activity;
        ButterKnife.bind(this, activity);
    }

    @NonNull
    @CheckResult
    public Observable<Void> addWeightButton() {
        return RxView.clicks(addWeight);
    }

    @NonNull
    @CheckResult
    public Observable<Float> openAddWeightDialog(@NonNull String initialValue) {
        final RxAlertDialog rxAlertDialog = RxAlertDialog.Builder.with(context)
                .title(R.string.add_weight_dialog_title)
                .customView(R.layout.add_weight_dialog_custom_view)
                .positiveButton(android.R.string.ok)
                .negativeButton(android.R.string.cancel)
                .show();
        TextView unitView = Preconditions.checkNotNull(rxAlertDialog.getCustomView())
                .findViewById(R.id.add_weight_unit);
        unitView.setText(context.getString(settingsModel.getBodyMassUnit().getNameRes()));
        return CustomDialogViewEditTextController.editTextValueOnOk(rxAlertDialog, initialValue, R.id.add_weight_value)
                .filter(s -> !Strings.isNullOrEmpty(s))
                .map(Float::valueOf);
    }

}
