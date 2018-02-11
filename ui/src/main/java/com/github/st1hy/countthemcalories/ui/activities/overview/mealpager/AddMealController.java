package com.github.st1hy.countthemcalories.ui.activities.overview.mealpager;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.view.View;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.ui.activities.addmeal.AddMealActivity;
import com.github.st1hy.countthemcalories.database.rx.timeperiod.DayData;
import com.github.st1hy.countthemcalories.ui.activities.overview.view.OverviewScreen;
import com.github.st1hy.countthemcalories.ui.inject.app.PerActivity;
import com.jakewharton.rxbinding.view.RxView;

import org.joda.time.DateTime;

import javax.inject.Inject;
import javax.inject.Provider;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.internal.DoubleCheck;
import rx.Observable;

@PerActivity
public class AddMealController {

    private final Activity activity;

    @BindView(R.id.overview_fab_add_meal)
    View addMeal;
    @Inject
    PagerModel pagerModel;
    @Inject
    OverviewScreen screen;

    private final Provider<Observable<Void>> addMealClicks = DoubleCheck.provider(
            () -> RxView.clicks(addMeal).share()
    );

    @Inject
    public AddMealController(@NonNull Activity activity) {
        this.activity = activity;
        ButterKnife.bind(this, activity);
    }

    @NonNull
    @CheckResult
    public Observable<Void> getAddNewMealObservable(@NonNull DateTime atDay) {
        return addMealClicks.get()
                .filter(any -> selectedDayMatches(atDay));
    }

    private boolean selectedDayMatches(@NonNull DateTime atDay) {
        DayData selectedDay = pagerModel.getSelectedDay();
        return selectedDay != null && atDay.equals(selectedDay.getDateTime());
    }

    public void addNewMeal(@NonNull DateTime atDay) {
        Intent intent = new Intent(activity, AddMealActivity.class);
        intent.putExtra(AddMealActivity.EXTRA_NEW_MEAL_DATE, atDay);
        activity.startActivity(intent);
    }

    public void closeFloatingMenu() {
        screen.closeFloatingMenu();
    }
}
