package com.github.st1hy.countthemcalories.activities.addmeal.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsActivity;
import com.github.st1hy.countthemcalories.activities.overview.view.OverviewActivity;
import com.github.st1hy.countthemcalories.core.withpicture.view.WithPictureActivity;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;

public class AddMealActivity extends WithPictureActivity implements AddMealView {
    public static final int REQUEST_PICK_INGREDIENT = 0x3903;

    @Inject
    AddMealPresenter presenter;
    @Inject
    IngredientsAdapter adapter;
    @Inject
    Picasso picasso;

    @Bind(R.id.add_meal_toolbar)
    Toolbar toolbar;
    @Bind(R.id.add_meal_image)
    ImageView mealImage;
    @Bind(R.id.add_meal_name)
    EditText name;
    @Bind(R.id.add_meal_ingredients_list)
    RecyclerView ingredientList;
    @Bind(R.id.add_meal_empty_ingredients)
    View emptyIngredients;
    @Bind(R.id.add_meal_button_add_ingredient)
    Button addIngredientButton;
    @Bind(R.id.add_meal_fab_add_ingredient)
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
        if (requestCode == REQUEST_PICK_INGREDIENT) {
            if (resultCode == RESULT_OK) {
                long ingredientId = data.getLongExtra(IngredientsActivity.EXTRA_INGREDIENT_TYPE_ID, -1L);
                if (ingredientId != -1L) {
                    adapter.onIngredientReceived(ingredientId);
                }
            }
        } else super.onActivityResult(requestCode, resultCode, data);
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
}
