package com.github.st1hy.countthemcalories.activities.overview.mealpager;

import com.github.st1hy.countthemcalories.activities.overview.model.TimePeriod;
import com.github.st1hy.countthemcalories.activities.overview.model.TimePeriodModel;
import com.github.st1hy.countthemcalories.core.BasicLifecycle;
import com.github.st1hy.countthemcalories.inject.PerActivity;
import com.github.st1hy.countthemcalories.inject.activities.overview.quantifier.datetime.NewMealDate;

import org.joda.time.DateTime;

import javax.inject.Inject;
import javax.inject.Provider;

import rx.subscriptions.CompositeSubscription;

@PerActivity
public class MealsPagerPresenter implements BasicLifecycle {

    @Inject
    MealPagerView view;
    @Inject
    PagerModel pagerModel;
    @Inject
    TimePeriodModel model;
    @Inject
    MealsPagerAdapter adapter;
    @Inject
    @NewMealDate
    Provider<DateTime> jumpToDate;

    private final CompositeSubscription subscriptions = new CompositeSubscription();

    @Inject
    public MealsPagerPresenter() {
    }

    @Override
    public void onStart() {
        subscriptions.add(
                model.updates()
                        .doOnNext(adapter::updateModel)
                        .map(this::intoPage)
                        .subscribe(page -> view.setCurrentItem(page, false))
        );
        subscriptions.add(
                pagerModel.selectedDay()
                        .subscribe(view::setTitle)
        );

    }

    private int intoPage(TimePeriod model) {
        DateTime dateTime = jumpToDate.get();
        if (dateTime != null) {
            return model.findDayPosition(dateTime);
        } else {
            return model.getCount() - 1;
        }
    }

    @Override
    public void onStop() {
        subscriptions.clear();
    }

}
