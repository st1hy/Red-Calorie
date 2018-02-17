package com.github.st1hy.countthemcalories.ui.activities.overview.mealpager;

import com.github.st1hy.countthemcalories.ui.activities.overview.presenter.TimePeriodLoader;
import com.github.st1hy.countthemcalories.ui.contract.CalorieStatistics;
import com.github.st1hy.countthemcalories.ui.contract.MealStatisticRepo;
import com.github.st1hy.countthemcalories.ui.contract.Schedulers;
import com.github.st1hy.countthemcalories.ui.core.BasicLifecycle;
import com.github.st1hy.countthemcalories.ui.inject.app.PerActivity;
import com.github.st1hy.countthemcalories.ui.inject.quantifier.datetime.NewMealDate;

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
    MealStatisticRepo repo;
    @Inject
    MealsPagerAdapter pagerAdapter;
    @Inject
    TimePeriodLoader timePeriodLoader;
    @Inject
    Schedulers schedulers;
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
                repo.updates().observeOn(schedulers.ui())
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

    private int defaultPage(CalorieStatistics model) {
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
