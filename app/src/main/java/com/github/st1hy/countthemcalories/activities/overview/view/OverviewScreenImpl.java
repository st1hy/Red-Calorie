package com.github.st1hy.countthemcalories.activities.overview.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.inject.activities.addmeal.fragment.AddMealFragmentModule;
import com.github.st1hy.countthemcalories.activities.addmeal.AddMealActivity;
import com.github.st1hy.countthemcalories.activities.addmeal.EditMealActivity;
import com.github.st1hy.countthemcalories.activities.mealdetail.MealDetailActivity;
import com.github.st1hy.countthemcalories.activities.overview.model.MealDetailAction;
import com.github.st1hy.countthemcalories.inject.PerActivity;
import com.github.st1hy.countthemcalories.core.activityresult.ActivityResult;
import com.github.st1hy.countthemcalories.core.activityresult.RxActivityResult;
import com.github.st1hy.countthemcalories.database.Meal;
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
    private final RxActivityResult rxActivityResult;

    @BindView(R.id.overview_fab)
    FloatingActionButton fab;
    @BindView(R.id.overview_total_energy)
    TextView totalEnergy;

    @Inject
    public OverviewScreenImpl(@NonNull Activity activity,
                              @NonNull RxActivityResult rxActivityResult) {
        this.activity = activity;
        this.rxActivityResult = rxActivityResult;
        ButterKnife.bind(this, activity);
    }

    @NonNull
    @Override
    public Observable<Void> getAddNewMealObservable() {
        return RxView.clicks(fab);
    }

    @Override
    public void addNewMeal() {
        Intent intent = new Intent(activity, AddMealActivity.class);
        activity.startActivity(intent);
    }

    @NonNull
    @Override
    public Observable<MealDetailAction> openMealDetails(@NonNull Meal meal,
                                                        @NonNull View sharedView) {
        Bundle startOptions = ActivityOptionsCompat
                .makeSceneTransitionAnimation(activity, sharedView, "overview-shared-view-image")
                .toBundle();
        Intent intent = new Intent(activity, MealDetailActivity.class);
        intent.putExtra(MealDetailActivity.EXTRA_MEAL_PARCEL, Parcels.wrap(meal));
        return rxActivityResult.from(activity)
                .startActivityForResult(intent, REQUEST_MEAL_DETAIL, startOptions)
                .map(new Func1<ActivityResult, MealDetailAction>() {
                    @Override
                    public MealDetailAction call(ActivityResult activityResult) {
                        return getMealDetailResult(activityResult);
                    }
                });
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
