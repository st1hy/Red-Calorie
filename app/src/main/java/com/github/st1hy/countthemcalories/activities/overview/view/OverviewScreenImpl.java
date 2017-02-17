package com.github.st1hy.countthemcalories.activities.overview.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.AddMealActivity;
import com.github.st1hy.countthemcalories.activities.addmeal.EditMealActivity;
import com.github.st1hy.countthemcalories.activities.mealdetail.MealDetailActivity;
import com.github.st1hy.countthemcalories.activities.overview.model.MealDetailAction;
import com.github.st1hy.countthemcalories.activities.overview.model.MealDetailParams;
import com.github.st1hy.countthemcalories.core.activityresult.ActivityLauncher;
import com.github.st1hy.countthemcalories.core.activityresult.ActivityResult;
import com.github.st1hy.countthemcalories.core.activityresult.StartParams;
import com.github.st1hy.countthemcalories.database.Meal;
import com.github.st1hy.countthemcalories.inject.PerActivity;
import com.github.st1hy.countthemcalories.inject.activities.addmeal.fragment.AddMealFragmentModule;
import com.jakewharton.rxbinding.view.RxView;

import org.parceler.Parcels;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.functions.Func1;

@PerActivity
public class OverviewScreenImpl implements OverviewScreen {

    private static final int REQUEST_MEAL_DETAIL = 0x300;

    @NonNull
    private final Activity activity;
    @NonNull
    private final ActivityLauncher activityLauncher;

    @BindView(R.id.overview_touch_overlay)
    View touchOverlay;
    @BindView(R.id.overview_fab_menu)
    FloatingActionsMenu fabMenu;
    @BindView(R.id.overview_fab_add_meal)
    View addMeal;
    @BindView(R.id.overview_total_energy)
    TextView totalEnergy;

    @Inject
    public OverviewScreenImpl(@NonNull Activity activity,
                              @NonNull ActivityLauncher activityLauncher) {
        this.activity = activity;
        this.activityLauncher = activityLauncher;
        ButterKnife.bind(this, activity);
    }

    @NonNull
    @Override
    public Observable<Void> getAddNewMealObservable() {
        return RxView.clicks(addMeal);
    }

    @Override
    public void addNewMeal() {
        Intent intent = new Intent(activity, AddMealActivity.class);
        activity.startActivity(intent);
    }

    @NonNull
    @Override
    public Observable.Transformer<MealDetailParams, MealDetailAction> openMealDetails() {
        return paramsObservable -> paramsObservable
                .map(this::getMealDetailsParams)
                .compose(activityLauncher.startActivityForResult(REQUEST_MEAL_DETAIL))
                .map(this::getMealDetailResult);
    }

    @NonNull
    private StartParams getMealDetailsParams(@NonNull MealDetailParams params) {
        Bundle startOptions = ActivityOptionsCompat
                .makeSceneTransitionAnimation(activity, params.getSharedView(), "overview-shared-view-image")
                .toBundle();
        Intent intent = new Intent(activity, MealDetailActivity.class);
        intent.putExtra(MealDetailActivity.EXTRA_MEAL_PARCEL, Parcels.wrap(params.getMeal()));
        return StartParams.of(intent, REQUEST_MEAL_DETAIL, startOptions);
    }

    @Override
    public void editMeal(@NonNull Meal meal) {
        Intent intent = new Intent(activity, EditMealActivity.class);
        intent.putExtra(AddMealFragmentModule.EXTRA_MEAL_PARCEL, Parcels.wrap(meal));
        activity.startActivity(intent);
    }

    @Override
    public void setTotalEnergy(@NonNull String energy) {
        totalEnergy.setText(energy);
    }

    @NonNull
    @Override
    public Observable<MotionEvent> touchOverlay(@NonNull Func1<? super MotionEvent, Boolean> handled) {
        return RxView.touches(touchOverlay, handled);
    }

    @Override
    public void closeFloatingMenu() {
        fabMenu.collapse();
    }

    @Override
    public boolean isFabMenuOpen() {
        return fabMenu.isExpanded();
    }

    @NonNull
    private MealDetailAction getMealDetailResult(ActivityResult activityResult) {
        final Intent data = activityResult.getData();
        if (data == null) return MealDetailAction.CANCELED;
        long mealId = data.getLongExtra(MealDetailActivity.EXTRA_RESULT_MEAL_ID_LONG, -2L);
        if (mealId == -2L) return MealDetailAction.CANCELED;
        MealDetailAction action;
        switch (activityResult.getResultCode()) {
            case MealDetailActivity.RESULT_EDIT:
                action = MealDetailAction.create(MealDetailAction.Type.EDIT, mealId);
                break;
            case MealDetailActivity.RESULT_DELETE:
                action = MealDetailAction.create(MealDetailAction.Type.DELETE, mealId);
                break;
            default:
                return MealDetailAction.CANCELED;
        }
        return action;
    }
}
