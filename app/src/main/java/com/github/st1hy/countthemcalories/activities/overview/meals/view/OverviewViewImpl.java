package com.github.st1hy.countthemcalories.activities.overview.meals.view;

import android.support.annotation.CheckResult;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.overview.model.MealDetailAction;
import com.github.st1hy.countthemcalories.activities.overview.model.MealDetailParams;
import com.github.st1hy.countthemcalories.activities.overview.view.OverviewScreen;
import com.github.st1hy.countthemcalories.core.state.Visibility;
import com.github.st1hy.countthemcalories.database.Meal;
import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.github.st1hy.countthemcalories.inject.quantifier.view.FragmentRootView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.functions.Func1;

@PerFragment
public class OverviewViewImpl implements OverviewView {

    @NonNull
    private final OverviewScreen screen;

    @BindView(R.id.overview_empty_text)
    View emptyView;
    @BindView(R.id.overview_empty_background)
    ImageView emptyBackground;

    @Inject
    public OverviewViewImpl(@NonNull OverviewScreen screen,
                            @NonNull @FragmentRootView View rootView) {
        this.screen = screen;
        ButterKnife.bind(this, rootView);
    }

    @Override
    public void setEmptyListVisibility(@NonNull Visibility visibility) {
        //noinspection WrongConstant
        emptyView.setVisibility(visibility.getVisibility());
    }

    @Override
    public void setEmptyBackground(@DrawableRes int drawableResId) {
        emptyBackground.setImageResource(drawableResId);
    }

    @Override
    @NonNull
    @CheckResult
    public Observable.Transformer<MealDetailParams, MealDetailAction> openMealDetails() {
        return screen.openMealDetails();
    }

    @Override
    public void editMeal(@NonNull Meal meal) {
        screen.editMeal(meal);
    }

    @NonNull
    @Override
    public Observable<MotionEvent> touchOverlay(@NonNull Func1<? super MotionEvent, Boolean> handled) {
        return screen.touchOverlay(handled);
    }

    @Override
    public void closeFloatingMenu() {
        screen.closeFloatingMenu();
    }

    @Override
    public boolean isFabMenuOpen() {
        return screen.isFabMenuOpen();
    }
}
