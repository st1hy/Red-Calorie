package com.github.st1hy.countthemcalories.activities.overview.addweight;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.st1hy.countthemcalories.activities.settings.model.SettingsModel;
import com.github.st1hy.countthemcalories.core.BasicLifecycle;
import com.github.st1hy.countthemcalories.database.Weight;
import com.github.st1hy.countthemcalories.inject.PerActivity;

import org.joda.time.DateTime;

import java.util.Locale;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

@PerActivity
public class AddWeightPresenter implements BasicLifecycle {

    @Inject
    AddWeightView view;
    @Inject
    RxDbWeightModel model;
    @Inject
    SettingsModel settingsModel;

    private final CompositeSubscription subscriptions = new CompositeSubscription();

    @Inject
    public AddWeightPresenter() {
    }

    @Override
    public void onStart() {
        subscriptions.add(
                view.addWeightButton()
                        .flatMap(any -> model.findOneByDate(today()))
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(this::convertToCurrentUnit)
                        .flatMap(weight -> view.openAddWeightDialog(weight))
                        .map(this::intoWeight)
                        .flatMap(weight -> model.insertOrUpdate(weight))
                        .subscribe()
        );
    }

    @NonNull
    private String convertToCurrentUnit(@Nullable Weight weight) {
        if (weight != null) {
            float base = settingsModel.getBodyMassUnit().getBase().floatValue();
            float value = weight.getWeight() / base;
            return String.format(Locale.UK, "%.2f", value);
        } else {
            return "";
        }
    }

    @Override
    public void onStop() {
        subscriptions.clear();
    }

    @NonNull
    private Weight intoWeight(float weightValue) {
        float value = settingsModel.getBodyMassUnit().getBase().floatValue() * weightValue;
        DateTime time = today();
        Weight weight = new Weight();
        weight.setMeasurementDate(time);
        weight.setWeight(value);
        return weight;
    }

    @NonNull
    private static DateTime today() {
        return DateTime.now().withTimeAtStartOfDay();
    }
}
