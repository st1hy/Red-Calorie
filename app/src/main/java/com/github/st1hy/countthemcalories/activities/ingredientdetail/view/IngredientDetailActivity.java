package com.github.st1hy.countthemcalories.activities.ingredientdetail.view;

import android.os.Bundle;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.view.IngredientDetailFragment;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.inject.IngredientDetailModule;
import com.github.st1hy.countthemcalories.core.baseview.BaseActivity;

import javax.inject.Inject;

public class IngredientDetailActivity extends BaseActivity {

    public static final int RESULT_REMOVE = 0x200;

    @Inject
    IngredientDetailFragment content; //adds fragment to view hierarchy

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ingredient_detail_activity);
        getAppComponent().newIngredientDetailActivityComponent(new IngredientDetailModule(this))
                .inject(this);
    }

}
