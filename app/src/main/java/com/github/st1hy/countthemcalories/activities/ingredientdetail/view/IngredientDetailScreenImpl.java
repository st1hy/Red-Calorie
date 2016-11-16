package com.github.st1hy.countthemcalories.activities.ingredientdetail.view;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.inject.PerActivity;
import com.github.st1hy.countthemcalories.database.Ingredient;

import org.parceler.Parcels;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;
import static com.github.st1hy.countthemcalories.inject.activities.ingredientdetail.fragment.IngredientsDetailFragmentModule.EXTRA_INGREDIENT;
import static com.github.st1hy.countthemcalories.inject.activities.ingredientdetail.fragment.IngredientsDetailFragmentModule.EXTRA_INGREDIENT_ID_LONG;
import static com.github.st1hy.countthemcalories.activities.ingredientdetail.IngredientDetailActivity.RESULT_REMOVE;

@PerActivity
public class IngredientDetailScreenImpl implements IngredientDetailScreen {

    @NonNull
    private final Activity activity;

    @Inject
    public IngredientDetailScreenImpl(@NonNull Activity activity) {
        this.activity = activity;
        ButterKnife.bind(this, activity);
    }

    @Override
    public void finishEdit(long ingredientId,
                           @NonNull Ingredient ingredient) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_INGREDIENT_ID_LONG, ingredientId);
        intent.putExtra(EXTRA_INGREDIENT, Parcels.wrap(ingredient));
        activity.setResult(RESULT_OK, intent);
        finishActivity();
    }

    @Override
    public void finishRemove(long ingredientId) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_INGREDIENT_ID_LONG, ingredientId);
        activity.setResult(RESULT_REMOVE, intent);
        finishActivity();
    }

    @OnClick(R.id.ingredient_detail_root)
    void onClickedOutside() {
        finishActivity();
    }

    private void finishActivity() {
        ActivityCompat.finishAfterTransition(activity);
    }
}
