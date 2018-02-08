package com.github.st1hy.countthemcalories.activities.ingredients;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.ingredients.presenter.IngredientsPresenter;
import com.github.st1hy.countthemcalories.core.baseview.BaseActivity;
import com.github.st1hy.countthemcalories.core.drawer.DrawerPresenter;
import com.github.st1hy.countthemcalories.inject.common.ActivityModule;

import java.util.Map;

import javax.inject.Inject;

public class IngredientsActivity extends BaseActivity {

    public static final String ACTION_SELECT_INGREDIENT = "Select ingredient";
    public static final String EXTRA_TAG_FILTER_STRING = "extra filter by tag string";

    public static final int REQUEST_EDIT = 0x128;
    public static final int REQUEST_ADD_INGREDIENT = 0x129;

    @Inject
    Map<String, Fragment> fragments; //inject fragments
    @Inject
    IngredientsPresenter presenter;
    @Inject
    DrawerPresenter drawerPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ingredients_activity);
        getAppComponent().newIngredientsActivityComponent(new ActivityModule(this))
                .inject(this);
    }


    @Override
    public void onStart() {
        super.onStart();
        presenter.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.onStop();
    }

    @Override
    public void onBackPressed() {
        if (drawerPresenter.onBackPressed()) super.onBackPressed();
    }
}
