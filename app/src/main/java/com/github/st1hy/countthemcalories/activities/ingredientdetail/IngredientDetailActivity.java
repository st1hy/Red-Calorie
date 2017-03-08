package com.github.st1hy.countthemcalories.activities.ingredientdetail;

import android.os.Bundle;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.IngredientDetailFragment;
import com.github.st1hy.countthemcalories.core.baseview.BaseActivity;
import com.github.st1hy.countthemcalories.inject.common.ActivityModule;

import javax.inject.Inject;

public class IngredientDetailActivity extends BaseActivity {

    public static final int RESULT_REMOVE = 0x200;
    public static final String EXTRA_INGREDIENT = "ingredient details ingredient";
    public static final String EXTRA_INGREDIENT_ID_LONG = "ingredient details extra id long";

    @Inject
    IngredientDetailFragment content; //adds fragment to view hierarchy

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ingredient_detail_activity);
        getAppComponent().newIngredientDetailActivityComponent(new ActivityModule(this))
                .inject(this);
    }

}
