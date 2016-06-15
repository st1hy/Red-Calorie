package com.github.st1hy.countthemcalories.activities.ingredients.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addingredient.view.AddIngredientActivity;
import com.github.st1hy.countthemcalories.activities.addingredient.view.SelectIngredientTypeActivity;
import com.github.st1hy.countthemcalories.activities.ingredients.inject.DaggerIngredientsActivityComponent;
import com.github.st1hy.countthemcalories.activities.ingredients.inject.IngredientsActivityComponent;
import com.github.st1hy.countthemcalories.activities.ingredients.inject.IngredientsActivityModule;
import com.github.st1hy.countthemcalories.activities.ingredients.presenter.IngredientsDaoAdapter;
import com.github.st1hy.countthemcalories.activities.ingredients.presenter.IngredientsPresenter;
import com.github.st1hy.countthemcalories.activities.ingredients.view.searchview.IngredientsSearchView;
import com.github.st1hy.countthemcalories.activities.ingredients.view.searchview.Query;
import com.github.st1hy.countthemcalories.core.command.view.UndoDrawerActivity;
import com.github.st1hy.countthemcalories.core.rx.RxAlertDialog;
import com.github.st1hy.countthemcalories.core.state.Visibility;
import com.github.st1hy.countthemcalories.database.parcel.IngredientTypeParcel;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxSearchView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.tokenautocomplete.TokenCompleteTextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class IngredientsActivity extends UndoDrawerActivity implements IngredientsView {
    public static final String ACTION_SELECT_INGREDIENT = "Select ingredient";
    public static final String EXTRA_INGREDIENT_TYPE_PARCEL = "extra ingredient type parcel";

    public static final int REQUEST_SELECT_TYPE = 0x127;
    public static final int REQUEST_EDIT = 0x128;
    public static final int REQUEST_ADD_INGREDIENT = 0x129;

    @Inject
    IngredientsPresenter presenter;
    @Inject
    IngredientsDaoAdapter adapter;

    @BindView(R.id.ingredients_fab)
    FloatingActionButton fab;
    @BindView(R.id.ingredients_empty)
    View emptyIngredients;
    @BindView(R.id.ingredients_empty_message)
    TextView emptyIngredientsText;
    @BindView(R.id.ingredients_content)
    RecyclerView recyclerView;
    @BindView(R.id.ingredients_root)
    CoordinatorLayout root;
    @BindView(R.id.ingredients_search_view)
    IngredientsSearchView search;

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

    CompositeSubscription subscriptions = new CompositeSubscription();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.ingredients_activity);
        ButterKnife.bind(this);
        getComponent().inject(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        Query[] queries = new Query[]{
                new Query("Marshall Weir"),
                new Query("Margaret Smith"),
                new Query("Max Jordan"),
                new Query("Meg Peterson"),
                new Query("Amanda Johnson"),
                new Query("Terry Anderson")
        };
        search.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, queries));
        search.setTokenListener(new TokenCompleteTextView.TokenListener<Query>() {
            @Override
            public void onTokenAdded(Query token) {
                Timber.d("Query added: %s", token);
            }

            @Override
            public void onTokenRemoved(Query token) {
                Timber.d("Query removed: %s", token);
            }
        });
        subscriptions.add(RxTextView.textChanges(search).subscribe(new Action1<CharSequence>() {
            @Override
            public void call(CharSequence charSequence) {
                Timber.d("Total query: %s", charSequence);
            }
        }));
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        subscriptions.clear();
        search.setTokenListener(null);
        search.setAdapter(null);
        search = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ingredient_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView = (SearchView) item.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE); //Fills toolbar
        adapter.onSearch(RxSearchView.queryTextChanges(searchView));
        return true;
    }

    @Override
    public void openNewIngredientScreen(@NonNull String action) {
        Intent intent = new Intent(this, AddIngredientActivity.class);
        intent.setAction(action);
        startActivityForResult(intent, REQUEST_ADD_INGREDIENT);
    }

    @Override
    public void setNoIngredientsVisibility(@NonNull Visibility visibility) {
        //noinspection WrongConstant
        emptyIngredients.setVisibility(visibility.getVisibility());
    }

    @NonNull
    @Override
    public Observable<Void> getOnAddIngredientClickedObservable() {
        return RxView.clicks(fab);
    }

    @Override
    public void setResultAndReturn(@NonNull IngredientTypeParcel ingredientTypeParcel) {
        Intent result = new Intent();
        result.putExtra(EXTRA_INGREDIENT_TYPE_PARCEL, ingredientTypeParcel);
        setResult(RESULT_OK, result);
        finish();
    }

    @Override
    public void selectIngredientType() {
        startActivityForResult(new Intent(this, SelectIngredientTypeActivity.class), REQUEST_SELECT_TYPE);
    }

    @NonNull
    @Override
    public Observable<Void> showUsedIngredientRemoveConfirmationDialog() {
        return RxAlertDialog.Builder.with(this)
                .title(R.string.ingredients_remove_ingredient_dialog_title)
                .message(R.string.ingredients_remove_ingredient_dialog_message)
                .positiveButton(android.R.string.yes)
                .negativeButton(android.R.string.no)
                .show()
                .observePositiveClick();
    }

    @Override
    public void openEditIngredientScreen(long requestID, IngredientTypeParcel ingredientParcel) {
        Intent intent = new Intent(this, AddIngredientActivity.class);
        intent.setAction(AddIngredientActivity.ACTION_EDIT);
        intent.putExtra(AddIngredientActivity.EXTRA_EDIT_REQUEST_ID_LONG, requestID);
        intent.putExtra(AddIngredientActivity.EXTRA_EDIT_INGREDIENT_PARCEL, ingredientParcel);
        startActivityForResult(intent, REQUEST_EDIT);
    }

    @Override
    public void scrollToPosition(int position) {
        recyclerView.scrollToPosition(position);
    }

    @Override
    public void setNoIngredientsMessage(@StringRes int noIngredientTextResId) {
        emptyIngredientsText.setText(noIngredientTextResId);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_SELECT_TYPE:
                presenter.onSelectIngredientTypeResult(resultCode);
                break;
            case REQUEST_ADD_INGREDIENT:
                presenter.onIngredientAdded(resultCode, data);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @NonNull
    @Override
    protected View getUndoRoot() {
        return root;
    }
}
