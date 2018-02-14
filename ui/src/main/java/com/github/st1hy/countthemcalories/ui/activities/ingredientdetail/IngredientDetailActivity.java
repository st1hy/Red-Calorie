package com.github.st1hy.countthemcalories.ui.activities.ingredientdetail;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.github.st1hy.countthemcalories.ui.R;
import com.github.st1hy.countthemcalories.ui.core.baseview.BaseActivity;
import com.github.st1hy.countthemcalories.ui.inject.core.ActivityModule;

import java.util.Map;

import javax.inject.Inject;

public class IngredientDetailActivity extends BaseActivity {

    public static final int RESULT_REMOVE = 0x200;
    public static final String EXTRA_INGREDIENT = "ingredient details ingredient";
    public static final String EXTRA_INGREDIENT_ID_LONG = "ingredient details extra id long";

    @Inject
    Map<String, Fragment> fragments; //adds fragments to view hierarchy

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ingredient_detail_activity);
        getAppComponent().newIngredientDetailActivityComponent(new ActivityModule(this))
                .inject(this);
    }

}
