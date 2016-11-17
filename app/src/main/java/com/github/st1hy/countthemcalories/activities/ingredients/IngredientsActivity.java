package com.github.st1hy.countthemcalories.activities.ingredients;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.IngredientsFragment;
import com.github.st1hy.countthemcalories.activities.ingredients.inject.DaggerIngredientsActivityComponent;
import com.github.st1hy.countthemcalories.inject.activities.ingredients.IngredientsActivityComponent;
import com.github.st1hy.countthemcalories.inject.activities.ingredients.IngredientsActivityModule;
import com.github.st1hy.countthemcalories.activities.ingredients.presenter.SearchSuggestionsAdapter;
import com.github.st1hy.countthemcalories.core.baseview.BaseActivity;
import com.github.st1hy.countthemcalories.core.drawer.DrawerPresenter;

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
    @Inject @Named("suggestions")
    SearchSuggestionsAdapter suggestionsAdapter;
    @Inject
    DrawerPresenter drawerPresenter;

    IngredientsActivityComponent component;

    @NonNull
    protected IngredientsActivityComponent getComponent() {
        if (component == null) {
            component = DaggerIngredientsActivityComponent.builder()
                    .applicationComponent(getAppComponent())
                    .ingredientsActivityModule(new IngredientsActivityModule(this))
                    .build();
        }
        return component;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ingredients_activity);
        getComponent().inject(this);
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
