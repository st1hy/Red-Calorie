package com.github.st1hy.countthemcalories.ui.activities.overview.addweight;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.st1hy.countthemcalories.contract.Schedulers;
import com.github.st1hy.countthemcalories.contract.model.DayStatistic;
import com.github.st1hy.countthemcalories.ui.activities.overview.mealpager.PagerModel;
import com.github.st1hy.countthemcalories.ui.activities.overview.view.OverviewScreen;
import com.github.st1hy.countthemcalories.ui.activities.settings.model.SettingsModel;
import com.github.st1hy.countthemcalories.ui.contract.MealStatisticRepo;
import com.github.st1hy.countthemcalories.ui.contract.Weight;
import com.github.st1hy.countthemcalories.ui.contract.WeightFactory;
import com.github.st1hy.countthemcalories.ui.contract.WeightRepo;
import com.github.st1hy.countthemcalories.ui.core.BasicLifecycle;
import com.github.st1hy.countthemcalories.ui.inject.app.PerActivity;
import com.github.st1hy.countthemcalories.ui.inject.app.TwoPlaces;

import org.joda.time.DateTime;

import java.text.DecimalFormat;

import javax.inject.Inject;

import rx.subscriptions.CompositeSubscription;

@PerActivity
public class AddWeightPresenter implements BasicLifecycle {

    @Inject
    AddWeightView view;
    @Inject
    OverviewScreen screen;
    @Inject
    WeightRepo weightRepo;
    @Inject
    SettingsModel settingsModel;
    @Inject
    PagerModel pagerModel;
    @Inject
    MealStatisticRepo statisticRepo;
    @Inject @TwoPlaces
    DecimalFormat decimalFormat;
    @Inject
    Schedulers schedulers;
    @Inject
    WeightFactory weightFactory;

    private final CompositeSubscription subscriptions = new CompositeSubscription();

    @Inject
    public AddWeightPresenter() {
    }

    @Override
    public void onStart() {
        subscriptions.add(
                view.addWeightButton()
                        .doOnNext(any -> screen.closeFloatingMenu())
                        .flatMap(any -> weightRepo.findOneByDate(currentDate()))
                        .observeOn(schedulers.ui())
                        .map(this::convertToCurrentUnit)
                        .flatMap(weight -> view.openAddWeightDialog(weight))
                        .map(this::intoWeight)
                        .flatMap(weight -> weightRepo.insertOrUpdate(weight))
                        .subscribe(any -> statisticRepo.refresh())
        );
    }

    @NonNull
    private String convertToCurrentUnit(@Nullable Weight weight) {
        if (weight != null) {
            double base = settingsModel.getBodyMassUnit().getBase();
            return decimalFormat.format(weight.getWeight() / base);
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
        float value = (float) (settingsModel.getBodyMassUnit().getBase() * weightValue);
        DateTime time = currentDate();
        Weight weight = weightFactory.newWeight();
        weight.setMeasurementDate(time);
        weight.setWeight(value);
        return weight;
    }

    @NonNull
    private DateTime currentDate() {
        DayStatistic selectedDay = pagerModel.getSelectedDay();
        DateTime dateTime = selectedDay != null ? selectedDay.getDateTime() : DateTime.now();
        return dateTime.withTimeAtStartOfDay();
    }
}
