package com.github.st1hy.countthemcalories.activities.addingredient.view;

import android.os.Bundle;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.core.baseview.BaseActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class SelectIngredientTypeActivity extends BaseActivity {
    public static final int RESULT_MEAL = 20;
    public static final int RESULT_DRINK = 21;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_ingredient_type_activity);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.select_ingredient_type_meal)
    void onMealClicked() {
        setResult(RESULT_MEAL);
        finish();
    }

    @OnClick(R.id.select_ingredient_type_drink)
    void onDrinkClicked() {
        setResult(RESULT_DRINK);
        finish();
    }
}
