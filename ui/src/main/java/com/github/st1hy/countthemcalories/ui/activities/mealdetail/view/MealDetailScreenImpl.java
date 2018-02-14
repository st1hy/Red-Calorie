package com.github.st1hy.countthemcalories.ui.activities.mealdetail.view;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.github.st1hy.countthemcalories.ui.R;
import com.github.st1hy.countthemcalories.ui.inject.app.PerActivity;
import com.github.st1hy.countthemcalories.ui.activities.mealdetail.MealDetailActivity;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;

@PerActivity
public class MealDetailScreenImpl implements MealDetailScreen {

    @NonNull
    private final Activity activity;

    @Inject
    public MealDetailScreenImpl(@NonNull Activity activity) {
        this.activity = activity;
        ButterKnife.bind(this, activity);
    }

    @Override
    public void editMealWithId(long mealId) {
        setResultAndFinish(MealDetailActivity.RESULT_EDIT, mealId);
    }

    @Override
    public void deleteMealWithId(long mealId) {
        setResultAndFinish(MealDetailActivity.RESULT_DELETE, mealId);
    }

    @Override
    public void copyMealWithId(long mealId) {
        setResultAndFinish(MealDetailActivity.RESULT_COPY, mealId);
    }

    private void setResultAndFinish(int resultCode, long mealId) {
        Intent intent = new Intent();
        intent.putExtra(MealDetailActivity.EXTRA_RESULT_MEAL_ID_LONG, mealId);
        activity.setResult(resultCode, intent);
        ActivityCompat.finishAfterTransition(activity);
    }

    @OnClick(R.id.meal_detail_root)
    void onClickedOutside() {
        ActivityCompat.finishAfterTransition(activity);
    }
}
