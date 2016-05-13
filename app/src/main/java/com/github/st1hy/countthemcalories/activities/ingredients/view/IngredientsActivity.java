package com.github.st1hy.countthemcalories.activities.ingredients.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addingredient.view.AddIngredientActivity;
import com.github.st1hy.countthemcalories.activities.ingredients.inject.DaggerIngredientsActivityComponent;
import com.github.st1hy.countthemcalories.activities.ingredients.inject.IngredientsActivityComponent;
import com.github.st1hy.countthemcalories.activities.ingredients.inject.IngredientsActivityModule;
import com.github.st1hy.countthemcalories.activities.ingredients.presenter.IngredientsDaoAdapter;
import com.github.st1hy.countthemcalories.activities.ingredients.presenter.IngredientsPresenter;
import com.github.st1hy.countthemcalories.core.drawer.view.DrawerActivity;
import com.github.st1hy.countthemcalories.core.state.Visibility;
import com.github.st1hy.countthemcalories.database.parcel.IngredientTypeParcel;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxSearchView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

public class IngredientsActivity extends DrawerActivity implements IngredientsView {
    public static final String ACTION_SELECT_INGREDIENT = "Select ingredient";
    public static final String EXTRA_INGREDIENT_TYPE_PARCEL = "extra ingredient type parcer";

    @Inject
    IngredientsPresenter presenter;
    @Inject
    IngredientsDaoAdapter adapter;

    @BindView(R.id.ingredients_fab)
    FloatingActionButton fab;
    @BindView(R.id.ingredients_no_ingredients_button)
    View noIngredientsButton;
    @BindView(R.id.ingredients_content)
    RecyclerView recyclerView;

    SearchView searchView;

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
        setContentView(R.layout.ingredients_activity);
        ButterKnife.bind(this);
        getComponent().inject(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ingredient_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView = (SearchView) item.getActionView();
        adapter.onSearch(RxSearchView.queryTextChanges(searchView));
        return true;
    }

    @Override
    public void openNewIngredientScreen() {
        Intent intent = new Intent(this, AddIngredientActivity.class);
        startActivity(intent);
    }

    @Override
    public void setNoIngredientButtonVisibility(@NonNull Visibility visibility) {
        //noinspection WrongConstant
        noIngredientsButton.setVisibility(visibility.getVisibility());
    }

    @NonNull
    @Override
    public Observable<Void> getOnAddIngredientClickedObservable() {
        return Observable.merge(RxView.clicks(fab), RxView.clicks(noIngredientsButton));
    }

    @Override
    public void setResultAndReturn(@NonNull IngredientTypeParcel ingredientTypeParcel) {
        Intent result = new Intent();
        result.putExtra(EXTRA_INGREDIENT_TYPE_PARCEL, ingredientTypeParcel);
        setResult(RESULT_OK, result);
        finish();
    }
}
