package com.github.st1hy.countthemcalories.activities.mealdetail.view;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.inject.PerActivity;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.github.st1hy.countthemcalories.activities.mealdetail.MealDetailActivity.EXTRA_RESULT_MEAL_ID_LONG;
import static com.github.st1hy.countthemcalories.activities.mealdetail.MealDetailActivity.RESULT_COPY;
import static com.github.st1hy.countthemcalories.activities.mealdetail.MealDetailActivity.RESULT_DELETE;
import static com.github.st1hy.countthemcalories.activities.mealdetail.MealDetailActivity.RESULT_EDIT;

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
        setResultAndFinish(RESULT_EDIT, mealId);
    }

    @Override
    public void deleteMealWithId(long mealId) {
        setResultAndFinish(RESULT_DELETE, mealId);
    }

    @Override
    public void copyMealWithId(long mealId) {
        setResultAndFinish(RESULT_COPY, mealId);
    }

    private void setResultAndFinish(int resultCode, long mealId) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_RESULT_MEAL_ID_LONG, mealId);
        activity.setResult(resultCode, intent);
        ActivityCompat.finishAfterTransition(activity);
    }

    @OnClick(R.id.meal_detail_root)
    void onClickedOutside() {
        ActivityCompat.finishAfterTransition(activity);
    }
}
