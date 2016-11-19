package com.github.st1hy.countthemcalories.activities.addingredient;

import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.core.baseview.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SelectIngredientTypeActivity extends BaseActivity {
    public static final int RESULT_MEAL = 20;
    public static final int RESULT_DRINK = 21;

    @BindView(R.id.select_ingredient_type_drink)
    ImageView drinkImage;
    @BindView(R.id.select_ingredient_type_meal)
    ImageView mealImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_ingredient_type_activity);
        ButterKnife.bind(this);
        drinkImage.setImageResource(R.drawable.ic_fizzy_drink);
        mealImage.setImageResource(R.drawable.ic_fork_and_knife_wide);
    }

    @OnClick(R.id.select_ingredient_type_meal)
    void onMealClicked() {
        setResult(RESULT_MEAL);
        ActivityCompat.finishAfterTransition(this);
    }

    @OnClick(R.id.select_ingredient_type_drink)
    void onDrinkClicked() {
        setResult(RESULT_DRINK);
        ActivityCompat.finishAfterTransition(this);
    }
}
