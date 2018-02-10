package com.github.st1hy.countthemcalories.activities.overview.mealpager;

import com.github.st1hy.countthemcalories.database.rx.timeperiod.TimePeriod;
import com.github.st1hy.countthemcalories.database.rx.timeperiod.TimePeriodModel;
import com.github.st1hy.countthemcalories.activities.overview.presenter.TimePeriodLoader;
import com.github.st1hy.countthemcalories.core.BasicLifecycle;
import com.github.st1hy.countthemcalories.inject.PerActivity;
import com.github.st1hy.countthemcalories.inject.quantifier.datetime.NewMealDate;

import org.joda.time.DateTime;

import javax.inject.Inject;
import javax.inject.Provider;

import rx.Observable;
import rx.subscriptions.CompositeSubscription;

@PerActivity
public class MealsPagerPresenter implements BasicLifecycle {

    @Inject
    MealPagerView view;
    @Inject
    PagerModel pagerModel;
    @Inject
    TimePeriodModel periodUpdates;
    @Inject
    MealsPagerAdapter pagerAdapter;
    @Inject
    TimePeriodLoader timePeriodLoader;
    @Inject
    @NewMealDate
    transient Provider<DateTime> jumpToDate;

    private final CompositeSubscription subscriptions = new CompositeSubscription();

    @Inject
    public MealsPagerPresenter() {
    }

    @Override
    public void onStart() {
        subscriptions.add(
                periodUpdates.updates()
                        .subscribe(pagerModel::updateModel)
        );
        subscriptions.add(
                view.onPageSelected().subscribe(pagerModel::setSelectedPage)
        );
        subscriptions.add(
                pagerModel.getSelectionEventChanges().subscribe(
                        event -> view.setCurrentItem(event.getPage(), event.isRequestScroll()))
        );
        subscriptions.add(
                pagerModel.timePeriodMostRecent()
                        .distinctUntilChanged()
                        .subscribe(pagerAdapter::updatePages)
        );
        subscriptions.add(
                Observable.combineLatest(
                        Observable.just(pagerModel.getSelectedPage())
                                .filter(page -> page > 0)
                                .mergeWith(
                                        pagerModel.getSelectedPageChanges()
                                ),
                        pagerModel.timePeriodMostRecent(),
                        (page, period) -> period.getDayDataAt(page))
                        .doOnNext(pagerModel::setLatestDay)
                        .subscribe(view::setTitle)
        );
        subscriptions.add(
                pagerModel.timePeriodMostRecent()
                        .map(this::defaultPage)
                        .subscribe(pagerModel::setSelectedPage)
        );
        timePeriodLoader.onStart();
    }

    private int defaultPage(TimePeriod model) {
        DateTime dateTime = jumpToDate.get();
        if (dateTime != null) {
            return model.findDayPosition(dateTime);
        } else {
            int selectedPage = pagerModel.getSelectedPage();
            if (selectedPage > 0) {
                return selectedPage;
            } else {
                return model.getDaysCount() - 1;
            }
        }
    }

    @Override
    public void onStop() {
        timePeriodLoader.onStop();
        subscriptions.clear();
    }

}
