package com.github.st1hy.countthemcalories.activities.overview.addweight;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.BasicLifecycle;
import com.github.st1hy.countthemcalories.database.Weight;
import com.github.st1hy.countthemcalories.inject.PerActivity;

import org.joda.time.DateTime;

import javax.inject.Inject;

import rx.subscriptions.CompositeSubscription;

@PerActivity
public class AddWeightPresenter implements BasicLifecycle {

    @Inject
    AddWeightView view;
    @Inject
    RxDbWeightModel model;

    private final CompositeSubscription subscriptions = new CompositeSubscription();

    @Inject
    public AddWeightPresenter() {
    }

    @Override
    public void onStart() {
        subscriptions.add(
                view.addWeightButton()
                        .flatMap(any -> view.openAddWeightDialog())
                        .map(this::intoWeight)
                        .flatMap(weight -> model.insertOrUpdate(weight))
                        .subscribe()
        );
    }

    @Override
    public void onStop() {
        subscriptions.clear();
    }

    @NonNull
    private Weight intoWeight(float weightValue) {
        DateTime time = DateTime.now().withTimeAtStartOfDay();
        Weight weight = new Weight();
        weight.setMeasurementDate(time);
        weight.setWeight(weightValue);
        return weight;
    }
}
