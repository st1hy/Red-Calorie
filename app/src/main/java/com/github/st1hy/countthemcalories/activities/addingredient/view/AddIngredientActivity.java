package com.github.st1hy.countthemcalories.activities.addingredient.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addingredient.inject.AddIngredientComponent;
import com.github.st1hy.countthemcalories.activities.addingredient.inject.AddIngredientModule;
import com.github.st1hy.countthemcalories.activities.addingredient.inject.DaggerAddIngredientComponent;
import com.github.st1hy.countthemcalories.activities.addingredient.presenter.AddIngredientPresenter;
import com.github.st1hy.countthemcalories.activities.addingredient.presenter.IngredientTagsAdapter;
import com.github.st1hy.countthemcalories.activities.tags.view.TagsActivity;
import com.github.st1hy.countthemcalories.core.ui.withpicture.view.WithPictureActivity;
import com.jakewharton.rxbinding.widget.RxTextView;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import timber.log.Timber;

public class AddIngredientActivity extends WithPictureActivity implements AddIngredientView {
    private static final int REQUEST_PICK_TAG = 0x2010;
    AddIngredientComponent component;

    @Inject
    AddIngredientPresenter presenter;
    @Inject
    IngredientTagsAdapter tagsPresenter;

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
    protected AddIngredientComponent getComponent(@Nullable Bundle savedInstanceState) {
        if (component == null) {
            component = DaggerAddIngredientComponent.builder()
                    .applicationComponent(getAppComponent())
                    .addIngredientModule(new AddIngredientModule(this, savedInstanceState))
                    .build();
        }
        return component;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_ingredient_activity);
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

        tagsRecycler.setAdapter(tagsPresenter);
        tagsRecycler.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        presenter.onSaveState(outState);
        tagsPresenter.onSaveState(outState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        tagsPresenter.onStart();
        presenter.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        tagsPresenter.onStop();
        presenter.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_ingredient_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return presenter.onClickedOnAction(item.getItemId()) ||  super.onOptionsItemSelected(item);
    }

    @Override
    public void setResultAndFinish() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void setSelectedUnitName(@NonNull String unitName) {
        selectUnit.setText(unitName);
    }

    @Override
    public void setName(@NonNull String name) {
        this.name.setText(name);
    }

    @Override
    public void setEnergyDensityValue(@NonNull String energyValue) {
        this.energyDensityValue.setText(energyValue);
    }

    @Override
    public void openSelectTagScreen() {
        Intent intent = new Intent(this, TagsActivity.class);
        intent.setAction(TagsActivity.ACTION_PICK_TAG);
        startActivityForResult(intent, REQUEST_PICK_TAG);
    }

    @Override
    public Observable<CharSequence> getNameObservable() {
        return RxTextView.textChanges(name);
    }

    @Override
    public Observable<CharSequence> getValueObservable() {
        return RxTextView.textChanges(energyDensityValue);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PICK_TAG) {
            if (resultCode == RESULT_OK) {
                long tagId = data.getLongExtra(TagsActivity.EXTRA_TAG_ID, -1);
                String tagName = data.getStringExtra(TagsActivity.EXTRA_TAG_NAME);
                if (tagId != -1 && tagName != null) {
                    tagsPresenter.onNewTagAdded(tagId, tagName);
                } else if (BuildConfig.DEBUG)
                    Timber.d("Tag intent returned but with wrong data; id: %s, name: '%s'",
                            tagId, tagName);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected ImageView getImageView() {
        return ingredientImage;
    }
}
