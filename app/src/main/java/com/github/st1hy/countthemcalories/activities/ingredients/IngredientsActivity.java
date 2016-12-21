package com.github.st1hy.countthemcalories.activities.ingredients;

import android.os.Bundle;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.IngredientsFragment;
import com.github.st1hy.countthemcalories.activities.ingredients.presenter.SearchSuggestionsAdapter;
import com.github.st1hy.countthemcalories.core.baseview.BaseActivity;
import com.github.st1hy.countthemcalories.core.drawer.DrawerPresenter;
import com.github.st1hy.countthemcalories.inject.activities.ingredients.IngredientsActivityModule;

import javax.inject.Inject;
import javax.inject.Named;

public class IngredientsActivity extends BaseActivity {

    public static final String ACTION_SELECT_INGREDIENT = "Select ingredient";
    public static final String EXTRA_INGREDIENT_TYPE_PARCEL = "extra ingredient template";
    public static final String EXTRA_TAG_FILTER_STRING = "extra filter by tag string";

    public static final int REQUEST_SELECT_TYPE = 0x127;
    public static final int REQUEST_EDIT = 0x128;
    public static final int REQUEST_ADD_INGREDIENT = 0x129;

    @Inject
    IngredientsFragment content;
    @Inject
    @Named("suggestions")
    SearchSuggestionsAdapter suggestionsAdapter;
    @Inject
    DrawerPresenter drawerPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ingredients_activity);
        getAppComponent().newIngredientsActivityComponent(new IngredientsActivityModule(this))
                .inject(this);
    }


    @Override
    public void onStart() {
        super.onStart();
        suggestionsAdapter.onStart();
        drawerPresenter.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        suggestionsAdapter.onStop();
        drawerPresenter.onStop();
    }

    @Override
    public void onBackPressed() {
        if (!drawerPresenter.onBackPressed()) super.onBackPressed();
    }
}
