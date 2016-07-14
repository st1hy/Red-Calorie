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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addingredient.inject.AddIngredientComponent;
import com.github.st1hy.countthemcalories.activities.addingredient.inject.AddIngredientModule;
import com.github.st1hy.countthemcalories.activities.addingredient.inject.DaggerAddIngredientComponent;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.presenter.AddIngredientPresenter;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.presenter.IngredientTagsAdapter;
import com.github.st1hy.countthemcalories.activities.tags.view.TagsActivity;
import com.github.st1hy.countthemcalories.core.withpicture.view.WithPictureActivity;
import com.google.common.base.Optional;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.Collection;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.subjects.PublishSubject;
import timber.log.Timber;

public class AddIngredientActivity extends WithPictureActivity implements AddIngredientView {
    public static final String RESULT_INGREDIENT_ID_LONG = "ingredient result id";

    private static final int REQUEST_PICK_TAG = 0x2010;
    AddIngredientComponent component;

    @Inject
    AddIngredientPresenter presenter;
    @Inject
    IngredientTagsAdapter tagsPresenter;

    @BindView(R.id.add_ingredient_toolbar)
    Toolbar toolbar;
    @BindView(R.id.add_ingredient_image)
    ImageView ingredientImage;
    @BindView(R.id.add_ingredient_name)
    EditText name;
    @BindView(R.id.add_ingredient_energy_density)
    EditText energyDensityValue;
    @BindView(R.id.add_ingredient_unit)
    TextView energyDensityUnit;
    @BindView(R.id.add_ingredient_categories_recycler)
    RecyclerView tagsRecycler;
    @BindView(R.id.add_ingredient_image_overlay_top)
    View imageOverlayTop;
    @BindView(R.id.add_ingredient_image_overlay_bottom)
    View imageOverlayBottom;

    final PublishSubject<Void> saveClickedSubject = PublishSubject.create();

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

        tagsRecycler.setAdapter(tagsPresenter);
        tagsRecycler.setLayoutManager(new LinearLayoutManager(this));
        tagsRecycler.setNestedScrollingEnabled(false);
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
        presenter.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_ingredient_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_save) {
            saveClickedSubject.onNext(null);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void setResultAndFinish(@NonNull Intent intent) {
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void setSelectedUnitName(@NonNull String unitName) {
        energyDensityUnit.setText(unitName);
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
    public void openSelectTagScreen(@NonNull Collection<String> tagNames) {
        Intent intent = new Intent(this, TagsActivity.class);
        intent.setAction(TagsActivity.ACTION_PICK_TAG);
        if (!tagNames.isEmpty()) {
            String[] tags = tagNames.toArray(new String[tagNames.size()]);
            intent.putExtra(TagsActivity.EXTRA_EXCLUDE_TAG_STRING_ARRAY, tags);
        }
        startActivityForResult(intent, REQUEST_PICK_TAG);
    }

    @NonNull
    @Override
    public Observable<CharSequence> getNameObservable() {
        return RxTextView.textChanges(name).skip(1);
    }

    @NonNull
    @Override
    public Observable<CharSequence> getValueObservable() {
        return RxTextView.textChanges(energyDensityValue).skip(1);
    }

    @Override
    public void showNameError(@NonNull Optional<Integer> errorResId) {
        if (errorResId.isPresent()) {
            name.setError(getString(errorResId.get()));
        } else {
            name.setError(null);
        }
    }

    @Override
    public void showValueError(@NonNull Optional<Integer> errorResId) {
        if (errorResId.isPresent()) {
            energyDensityValue.setError(getString(errorResId.get()));
        } else {
            energyDensityValue.setError(null);
        }
    }

    @NonNull
    @Override
    public ImageView getImageView() {
        return ingredientImage;
    }

    @Override
    public void requestFocusToName() {
        name.requestFocus();
    }

    @Override
    public void requestFocusToValue() {
        energyDensityValue.requestFocus();
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
    public void showImageOverlay() {
        imageOverlayBottom.setVisibility(View.VISIBLE);
        imageOverlayTop.setVisibility(View.VISIBLE);
    }

    @NonNull
    @Override
    public Observable<Void> getSelectPictureObservable() {
        return RxView.clicks(ingredientImage);
    }

    @NonNull
    @Override
    public Observable<Void> getSaveObservable() {
        return saveClickedSubject;
    }
}
