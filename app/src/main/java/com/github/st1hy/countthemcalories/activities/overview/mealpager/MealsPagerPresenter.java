package com.github.st1hy.countthemcalories.activities.overview.mealpager;

import com.github.st1hy.countthemcalories.activities.overview.model.TimePeriodModel;
import com.github.st1hy.countthemcalories.core.BasicLifecycle;
import com.github.st1hy.countthemcalories.inject.PerActivity;

import javax.inject.Inject;

import rx.Observable;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

@PerActivity
public class MealsPagerPresenter implements BasicLifecycle {

    @Inject
    MealPagerView view;
    @Inject
    TimePeriodModel model;
    @Inject
    MealsPagerAdapter adapter;

    private final CompositeSubscription subscriptions = new CompositeSubscription();

    @Inject
    public MealsPagerPresenter() {
    }

    @Override
    public void onStart() {
        subscriptions.add(
                model.getRecentPeriod()
                        .doOnNext(adapter::updateModel)
                        .doOnNext(model -> view.setCurrentItem(model.getCount() - 1, false))
                        .subscribe()
        );
        subscriptions.add(
                Observable.combineLatest(view.onPageSelected(),
                        model.getRecentPeriod(),
                        (page, period) -> {
                            Timber.d("Selected %d %s", page, period.getDayDataAt(page).getDateTime());
                            view.setTitle(period.getDayDataAt(page));
                            return null;
                        })
                        .subscribe()
        );

    }

    @Override
    public void onStop() {
        subscriptions.clear();
    }

}
