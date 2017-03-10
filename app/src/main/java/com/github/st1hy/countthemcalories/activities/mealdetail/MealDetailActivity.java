package com.github.st1hy.countthemcalories.activities.mealdetail;

import android.os.Bundle;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.mealdetail.fragment.MealDetailFragment;
import com.github.st1hy.countthemcalories.core.baseview.BaseActivity;
import com.github.st1hy.countthemcalories.inject.common.ActivityModule;

import javax.inject.Inject;

public class MealDetailActivity extends BaseActivity {

    public static final String EXTRA_MEAL_PARCEL = "meal detail parcel";
    public static final int RESULT_EDIT = 0x51;
    public static final int RESULT_DELETE = 0x52;
    public static final String EXTRA_RESULT_MEAL_ID_LONG = "extra result meal id";

    @Inject
    MealDetailFragment content; //injects fragment

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meal_detail_activity);
        getAppComponent().newMealDetailActivityComponent(new ActivityModule(this))
                .inject(this);
    }
}
