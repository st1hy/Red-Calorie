package com.github.st1hy.countthemcalories.activities.addmeal.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.inject.AddMealActivityComponent;
import com.github.st1hy.countthemcalories.activities.addmeal.inject.AddMealActivityModule;
import com.github.st1hy.countthemcalories.activities.addmeal.inject.DaggerAddMealActivityComponent;
import com.github.st1hy.countthemcalories.activities.addmeal.presenter.AddMealPresenter;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsActivity;
import com.github.st1hy.countthemcalories.activities.overview.view.OverviewActivity;
import com.github.st1hy.countthemcalories.core.ui.withpicture.view.WithPictureActivity;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AddMealActivity extends WithPictureActivity implements AddMealView {
    public static final int REQUEST_PICK_INGREDIENT = 0x3903;
    public static final String EXTRA_INGREDIENT_TYPE_ID = "extra ingredient type id";

    @Inject
    AddMealPresenter presenter;
    @Inject
    Picasso picasso;

    @Bind(R.id.add_meal_toolbar)
    Toolbar toolbar;
    @Bind(R.id.add_meal_image)
    ImageView mealImage;
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
    protected AddMealActivityComponent getComponent() {
        if (component == null) {
            component = DaggerAddMealActivityComponent.builder()
                    .applicationComponent(getAppComponent())
                    .addMealActivityModule(new AddMealActivityModule(this))
                    .build();
        }
        return component;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_meal_activity);
        ButterKnife.bind(this);
        getComponent().inject(this);
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
        ingredientList.setAdapter(presenter.getIngredientListAdapter());
        ingredientList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        final View.OnClickListener onAddIngredientClicked = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onAddNewIngredientClicked();
            }
        };
        addIngredientFab.setOnClickListener(onAddIngredientClicked);
        addIngredientButton.setOnClickListener(onAddIngredientClicked);
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
    public void openOverviewActivity() {
        Intent intent = new Intent(this, OverviewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PICK_INGREDIENT) {
            if (resultCode == RESULT_OK) {
                int intExtra = data.getIntExtra(EXTRA_INGREDIENT_TYPE_ID, -1);
                presenter.onIngredientReceived(intExtra);
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
