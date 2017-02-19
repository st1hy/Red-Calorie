package com.github.st1hy.countthemcalories.activities.overview.mealpager;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.view.View;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.AddMealActivity;
import com.github.st1hy.countthemcalories.core.rx.Filters;
import com.github.st1hy.countthemcalories.core.rx.Functions;
import com.github.st1hy.countthemcalories.inject.PerActivity;
import com.github.st1hy.countthemcalories.inject.activities.addmeal.fragment.AddMealFragmentModule;
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
        return Observable.combineLatest(addMealClicks.get(), pagerModel.selectedDay(),
                (aVoid, dayData) -> dayData.getDateTime().equals(atDay))
                .filter(Filters.equalTo(true))
                .map(Functions.INTO_VOID);
    }

    public void addNewMeal(@NonNull DateTime atDay) {
        Intent intent = new Intent(activity, AddMealActivity.class);
        intent.putExtra(AddMealFragmentModule.EXTRA_NEW_MEAL_DATE, atDay);
        activity.startActivity(intent);
    }
}
