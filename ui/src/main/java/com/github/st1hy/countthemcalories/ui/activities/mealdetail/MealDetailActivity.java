package com.github.st1hy.countthemcalories.ui.activities.mealdetail;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.github.st1hy.countthemcalories.ui.R;
import com.github.st1hy.countthemcalories.ui.core.baseview.BaseActivity;
import com.github.st1hy.countthemcalories.ui.inject.core.ActivityModule;

import java.util.Map;

import javax.inject.Inject;

public class MealDetailActivity extends BaseActivity {

    public static final String EXTRA_MEAL_PARCEL = "meal detail parcel";
    public static final int RESULT_EDIT = 0x51;
    public static final int RESULT_DELETE = 0x52;
    public static final int RESULT_COPY = 0x53;
    public static final String EXTRA_RESULT_MEAL_ID_LONG = "extra result meal id";

    @Inject
    Map<String, Fragment> fragments; //injects fragments

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meal_detail_activity);
        getAppComponent().newMealDetailActivityComponent(new ActivityModule(this))
                .inject(this);
    }
}
