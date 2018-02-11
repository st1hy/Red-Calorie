package com.github.st1hy.countthemcalories.ui.activities.addmeal.fragment.view;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.ui.activities.addmeal.view.AddMealScreen;
import com.github.st1hy.countthemcalories.ui.core.state.Visibility;

import org.joda.time.DateTime;

import rx.Observable;

public interface AddMealView extends AddMealScreen {

    void setName(@NonNull String name);

    @NonNull
    @CheckResult
    Observable<CharSequence> getNameObservable();

    void setEmptyIngredientsVisibility(@NonNull Visibility visibility);

    void scrollTo(int itemPosition);

    void setTotalEnergy(@NonNull String totalEnergy);

    void setHint(@NonNull String mealNameNow);

    @NonNull
    @CheckResult
    Observable<Void> mealTimeClicked();

    void setMealTime(@NonNull String time);

    @NonNull
    @CheckResult
    Observable.Transformer<Void, DateTime> openTimePicker(DateTime currentTime);
}
