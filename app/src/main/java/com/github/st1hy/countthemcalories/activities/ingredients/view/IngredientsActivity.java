package com.github.st1hy.countthemcalories.activities.ingredients.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.AddIngredientType;
import com.github.st1hy.countthemcalories.activities.addingredient.inject.AddIngredientModule;
import com.github.st1hy.countthemcalories.activities.addingredient.view.AddIngredientActivity;
import com.github.st1hy.countthemcalories.activities.addingredient.view.EditIngredientActivity;
import com.github.st1hy.countthemcalories.activities.addingredient.view.SelectIngredientTypeActivity;
import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealActivity;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.view.IngredientsFragment;
import com.github.st1hy.countthemcalories.activities.ingredients.inject.DaggerIngredientsActivityComponent;
import com.github.st1hy.countthemcalories.activities.ingredients.inject.IngredientsActivityComponent;
import com.github.st1hy.countthemcalories.activities.ingredients.inject.IngredientsActivityModule;
import com.github.st1hy.countthemcalories.activities.ingredients.presenter.SearchSuggestionsAdapter;
import com.github.st1hy.countthemcalories.core.baseview.BaseActivity;
import com.github.st1hy.countthemcalories.core.command.view.UndoDrawerActivity;
import com.github.st1hy.countthemcalories.core.tokensearch.SearchResult;
import com.github.st1hy.countthemcalories.core.tokensearch.TokenSearchView;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.jakewharton.rxbinding.view.RxView;

import org.parceler.Parcels;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

import static com.github.st1hy.countthemcalories.activities.addingredient.view.AddIngredientActivity.ARG_EDIT_INGREDIENT_PARCEL;
import static com.github.st1hy.countthemcalories.activities.addingredient.view.AddIngredientActivity.ARG_EDIT_REQUEST_ID_LONG;

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
    SearchSuggestionsAdapter suggestionsAdapter;

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
    }

    @Override
    public void onStop() {
        super.onStop();
        suggestionsAdapter.onStop();
    }

}
