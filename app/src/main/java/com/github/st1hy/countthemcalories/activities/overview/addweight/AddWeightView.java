package com.github.st1hy.countthemcalories.activities.overview.addweight;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.view.View;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.core.rx.RxAlertDialog;
import com.github.st1hy.countthemcalories.inject.PerActivity;
import com.google.common.base.Strings;
import com.jakewharton.rxbinding.view.RxView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

import static com.github.st1hy.countthemcalories.core.dialog.CustomDialogViewEditTextController.editTextValueOnOk;

@PerActivity
public class AddWeightView {

    @BindView(R.id.overview_fab_add_weight)
    View addWeight;

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
    public Observable<Float> openAddWeightDialog() {
        final RxAlertDialog rxAlertDialog = RxAlertDialog.Builder.with(context)
                .title(R.string.add_weight_dialog_title)
                .customView(R.layout.add_weight_dialog_custom_view)
                .positiveButton(android.R.string.ok)
                .negativeButton(android.R.string.cancel)
                .show();
        return editTextValueOnOk(rxAlertDialog, "", R.id.add_weight_value)
                .filter(s -> !Strings.isNullOrEmpty(s))
                .map(Float::valueOf);
    }

}
