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
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.inject.AddIngredientFragmentModule;
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

public class IngredientsActivity extends UndoDrawerActivity implements IngredientsScreen, SearchSuggestionsView {

    public static final String ACTION_SELECT_INGREDIENT = "Select ingredient";
    public static final String EXTRA_INGREDIENT_TYPE_PARCEL = "extra ingredient template";
    public static final String EXTRA_TAG_FILTER_STRING = "extra filter by tag string";

    public static final int REQUEST_SELECT_TYPE = 0x127;
    public static final int REQUEST_EDIT = 0x128;
    public static final int REQUEST_ADD_INGREDIENT = 0x129;

    @Inject
    SearchSuggestionsAdapter suggestionsAdapter;
    @Inject
    IngredientsFragment content;
    @Inject
    Observable<SearchResult> searchResultObservable;

    @BindView(R.id.ingredients_fab)
    FloatingActionButton fab;
    @BindView(R.id.ingredients_search_view)
    TokenSearchView searchView;

    @BindView(R.id.ingredients_root)
    CoordinatorLayout root;

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
        ButterKnife.bind(this);
        getComponent().inject(this);
        onBind();
    }

    @Override
    protected void onBind() {
        super.onBind();
        searchView.setSuggestionsAdapter(suggestionsAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        suggestionsAdapter.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        suggestionsAdapter.onStop();
    }

    @Override
    public void openNewIngredientScreen(@NonNull AddIngredientType type, @NonNull String extraName) {
        Intent intent = new Intent(this, AddIngredientActivity.class);
        intent.setAction(type.getAction());
        intent.putExtra(AddIngredientModule.EXTRA_NAME, extraName);
        startActivityForResult(intent, REQUEST_ADD_INGREDIENT);
    }

    @NonNull
    @Override
    public Observable<Void> getOnAddIngredientClickedObservable() {
        return RxView.clicks(fab);
    }

    @Override
    public void onIngredientSelected(@NonNull IngredientTemplate ingredientTemplate) {
        Intent result = new Intent();
        result.putExtra(EXTRA_INGREDIENT_TYPE_PARCEL, Parcels.wrap(ingredientTemplate));
        setResult(RESULT_OK, result);
        finish();
    }

    @Override
    public void openNewMealScreen(@NonNull IngredientTemplate ingredientTemplate) {
        Intent intent = new Intent(this, AddMealActivity.class);
        intent.putExtra(EXTRA_INGREDIENT_TYPE_PARCEL, Parcels.wrap(ingredientTemplate));
        startActivity(intent);
    }

    @Override
    public void selectIngredientType() {
        startActivityForResult(new Intent(this, SelectIngredientTypeActivity.class), REQUEST_SELECT_TYPE);
    }

    @Override
    public void openEditIngredientScreen(long requestID, @NonNull IngredientTemplate ingredientTemplate) {
        Intent intent = new Intent(this, EditIngredientActivity.class);
        intent.putExtra(AddIngredientFragmentModule.ARG_EDIT_REQUEST_ID_LONG, requestID);
        intent.putExtra(EXTRA_INGREDIENT_TYPE_PARCEL, Parcels.wrap(ingredientTemplate));
        startActivityForResult(intent, REQUEST_EDIT);
    }

    @Override
    public void expandSearchBar() {
        searchView.expand(false);
    }

    @NonNull
    @Override
    public Observable<SearchResult> getSearchObservable() {
        return searchResultObservable;
    }

    @Override
    public void setSearchQuery(@NonNull String query, @NonNull List<String> tokens) {
        searchView.setQuery(query, tokens);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_SELECT_TYPE:
                onSelectIngredientTypeResult(resultCode);
                break;
            case REQUEST_ADD_INGREDIENT:
                onIngredientAddedResult(resultCode, data);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    void onIngredientAddedResult(int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            long addedIngredientId = data.getLongExtra(AddIngredientActivity.RESULT_INGREDIENT_ID_LONG, -1L);
            if (addedIngredientId != -1L)
                content.onIngredientAdded(addedIngredientId);
        }
    }

    void onSelectIngredientTypeResult(int resultCode) {
        switch (resultCode) {
            case SelectIngredientTypeActivity.RESULT_DRINK:
                content.onSelectedNewIngredientType(AddIngredientType.DRINK);
                break;
            case SelectIngredientTypeActivity.RESULT_MEAL:
                content.onSelectedNewIngredientType(AddIngredientType.MEAL);
                break;
        }
    }

    @NonNull
    @Override
    protected View getUndoRoot() {
        return root;
    }

    @NonNull
    public TokenSearchView getSearchView() {
        return searchView;
    }
}
