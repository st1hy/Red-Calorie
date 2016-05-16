package com.github.st1hy.countthemcalories.activities.addmeal.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.inject.AddMealActivityComponent;
import com.github.st1hy.countthemcalories.activities.addmeal.inject.AddMealActivityModule;
import com.github.st1hy.countthemcalories.activities.addmeal.inject.DaggerAddMealActivityComponent;
import com.github.st1hy.countthemcalories.activities.addmeal.presenter.AddMealPresenter;
import com.github.st1hy.countthemcalories.activities.addmeal.presenter.IngredientsAdapter;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.view.IngredientDetailsActivity;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsActivity;
import com.github.st1hy.countthemcalories.activities.overview.view.OverviewActivity;
import com.github.st1hy.countthemcalories.core.state.Visibility;
import com.github.st1hy.countthemcalories.core.withpicture.view.WithPictureActivity;
import com.github.st1hy.countthemcalories.database.parcel.IngredientTypeParcel;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.math.BigDecimal;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

public class AddMealActivity extends WithPictureActivity implements AddMealView {
    public static final int REQUEST_PICK_INGREDIENT = 0x3903;
    public static final int REQUEST_EDIT_INGREDIENT = 0x3904;

    @Inject
    AddMealPresenter presenter;
    @Inject
    IngredientsAdapter adapter;

    @BindView(R.id.add_meal_toolbar)
    Toolbar toolbar;
    @BindView(R.id.add_meal_image)
    ImageView mealImage;
    @BindView(R.id.add_meal_name)
    EditText name;
    @BindView(R.id.add_meal_ingredients_list)
    RecyclerView ingredientList;
    @BindView(R.id.add_meal_empty_ingredients)
    View emptyIngredients;
    @BindView(R.id.add_meal_button_add_ingredient)
    Button addIngredientButton;
    @BindView(R.id.add_meal_fab_add_ingredient)
    FloatingActionButton addIngredientFab;

    AddMealActivityComponent component;

    @NonNull
    protected AddMealActivityComponent getComponent(@Nullable Bundle savedInstanceState) {
        if (component == null) {
            component = DaggerAddMealActivityComponent.builder()
                    .applicationComponent(getAppComponent())
                    .addMealActivityModule(new AddMealActivityModule(this, savedInstanceState))
                    .build();
        }
        return component;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_meal_activity);
        ButterKnife.bind(this);
        getComponent(savedInstanceState).inject(this);
        setSupportActionBar(toolbar);
        assertNotNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mealImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onImageClicked();
            }
        });
        ingredientList.setAdapter(adapter);
        ingredientList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        ingredientList.setNestedScrollingEnabled(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.onStart();
    }

    @Override
    public void setName(@NonNull String name) {
        this.name.setText(name);
    }

    @NonNull
    @Override
    public Observable<CharSequence> getNameObservable() {
        return RxTextView.textChanges(name);
    }

    @NonNull
    @Override
    public Observable<Void> getAddIngredientObservable() {
        return Observable.merge(RxView.clicks(addIngredientButton), RxView.clicks(addIngredientFab));
    }

    @Override
    public void setEmptyIngredientsVisibility(@NonNull Visibility visibility) {
        //noinspection WrongConstant
        emptyIngredients.setVisibility(visibility.getVisibility());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_meal_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return presenter.onClickedOnAction(item.getItemId()) || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        presenter.onSaveState(outState);
    }

    @Override
    public void openOverviewActivity() {
        Intent intent = new Intent(this, OverviewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!presenter.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void openAddIngredient() {
        Intent intent = new Intent(this, IngredientsActivity.class);
        intent.setAction(IngredientsActivity.ACTION_SELECT_INGREDIENT);
        startActivityForResult(intent, REQUEST_PICK_INGREDIENT);
    }

    @Override
    protected ImageView getImageView() {
        return mealImage;
    }

    @Override
    public void showIngredientDetails(long requestId,
                                      @NonNull IngredientTypeParcel ingredientParcel,
                                      @NonNull BigDecimal amount,
                                      @Nullable View sharedElement,
                                      @Nullable String sharedElementName) {
        Bundle startOptions = null;
        if (sharedElement != null) {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, sharedElement, sharedElementName);
            startOptions = options.toBundle();
        }
        Intent intent = new Intent(this, IngredientDetailsActivity.class);
        intent.setAction(IngredientDetailsActivity.ACTION_EDIT_INGREDIENT);
        intent.putExtra(IngredientDetailsActivity.EXTRA_INGREDIENT_ID_LONG, requestId);
        intent.putExtra(IngredientDetailsActivity.EXTRA_INGREDIENT_TEMPLATE_PARCEL, ingredientParcel);
        intent.putExtra(IngredientDetailsActivity.EXTRA_INGREDIENT_AMOUNT_BIGDECIMAL, amount.toPlainString());
        startActivityForResult(intent, REQUEST_EDIT_INGREDIENT, startOptions);
    }

    @Override
    public void scrollTo(int itemPosition) {
        ingredientList.scrollToPosition(itemPosition);
    }
}
