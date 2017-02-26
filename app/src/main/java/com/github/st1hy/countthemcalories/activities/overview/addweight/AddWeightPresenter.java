package com.github.st1hy.countthemcalories.activities.overview.addweight;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.st1hy.countthemcalories.activities.overview.mealpager.PagerModel;
import com.github.st1hy.countthemcalories.activities.overview.model.DayData;
import com.github.st1hy.countthemcalories.activities.overview.model.TimePeriodModel;
import com.github.st1hy.countthemcalories.activities.settings.model.SettingsModel;
import com.github.st1hy.countthemcalories.core.BasicLifecycle;
import com.github.st1hy.countthemcalories.database.Weight;
import com.github.st1hy.countthemcalories.inject.PerActivity;

import org.joda.time.DateTime;

import java.math.BigDecimal;

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
    @Inject
    PagerModel pagerModel;
    @Inject
    TimePeriodModel timePeriodModel;

    private final CompositeSubscription subscriptions = new CompositeSubscription();

    @Inject
    public AddWeightPresenter() {
    }

    @Override
    public void onStart() {
        subscriptions.add(
                view.addWeightButton()
                        .flatMap(any -> model.findOneByDate(currentDate()))
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(this::convertToCurrentUnit)
                        .flatMap(weight -> view.openAddWeightDialog(weight))
                        .map(this::intoWeight)
                        .flatMap(weight -> model.insertOrUpdate(weight))
                        .subscribe(any -> timePeriodModel.refresh())
        );
    }

    @NonNull
    private String convertToCurrentUnit(@Nullable Weight weight) {
        if (weight != null) {
            BigDecimal base = settingsModel.getBodyMassUnit().getBase();
            return BigDecimal.valueOf(weight.getWeight())
                    .divide(base, 2, BigDecimal.ROUND_HALF_UP)
                    .stripTrailingZeros()
                    .toPlainString();
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
        DateTime time = currentDate();
        Weight weight = new Weight();
        weight.setMeasurementDate(time);
        weight.setWeight(value);
        return weight;
    }

    @NonNull
    private DateTime currentDate() {
        DayData selectedDay = pagerModel.getSelectedDay();
        DateTime dateTime = selectedDay != null ? selectedDay.getDateTime() : DateTime.now();
        return dateTime.withTimeAtStartOfDay();
    }
}
