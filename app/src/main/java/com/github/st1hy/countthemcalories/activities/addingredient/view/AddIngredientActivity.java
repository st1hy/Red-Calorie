package com.github.st1hy.countthemcalories.activities.addingredient.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addingredient.inject.AddIngredientComponent;
import com.github.st1hy.countthemcalories.activities.addingredient.inject.AddIngredientModule;
import com.github.st1hy.countthemcalories.activities.addingredient.inject.DaggerAddIngredientComponent;
import com.github.st1hy.countthemcalories.activities.addingredient.presenter.AddIngredientPresenter;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsActivity;
import com.github.st1hy.countthemcalories.activities.withpicture.view.WithPictureActivity;
import com.github.st1hy.countthemcalories.activities.withpicture.presenter.WithPicturePresenter;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AddIngredientActivity extends WithPictureActivity implements AddIngredientView {
    AddIngredientComponent component;

    @Inject
    AddIngredientPresenter presenter;
    @Inject
    Picasso picasso;

    @Bind(R.id.add_ingredient_toolbar)
    Toolbar toolbar;
    @Bind(R.id.add_ingredient_image)
    ImageView ingredientImage;
    @Bind(R.id.add_ingredient_name)
    EditText name;
    @Bind(R.id.add_ingredient_energy_density)
    EditText energyDensityValue;
    @Bind(R.id.add_ingredient_select_unit)
    Button selectUnit;
    @Bind(R.id.add_ingredient_categories_recycler)
    RecyclerView tagsRecycler;

    @NonNull
    protected AddIngredientComponent getComponent() {
        if (component == null) {
            component = DaggerAddIngredientComponent.builder()
                    .applicationComponent(getAppComponent())
                    .addIngredientModule(new AddIngredientModule(this))
                    .build();
        }
        return component;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_ingredient_activity);
        ButterKnife.bind(this);
        getComponent().inject(this);
        setSupportActionBar(toolbar);
        assertNotNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        ingredientImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onImageClicked();
            }
        });
        selectUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onSelectUnitClicked();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_ingredient_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return presenter.onClickedOnAction(item.getItemId()) || super.onOptionsItemSelected(item);
    }

    @Override
    public void openIngredientsScreen() {
        Intent intent = new Intent(this, IngredientsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void showAvailableUnitsDialog() {
        //TODO
    }

    @Override
    protected ImageView getImageView() {
        return ingredientImage;
    }

    @Override
    protected Picasso getPicasso() {
        return picasso;
    }

    @Override
    protected WithPicturePresenter getPresenter() {
        return presenter;
    }
}
