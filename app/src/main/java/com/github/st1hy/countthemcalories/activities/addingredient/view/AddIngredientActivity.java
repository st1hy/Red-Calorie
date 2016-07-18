package com.github.st1hy.countthemcalories.activities.addingredient.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.BuildConfig;
import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.view.AddIngredientFragment;
import com.github.st1hy.countthemcalories.activities.addingredient.inject.AddIngredientComponent;
import com.github.st1hy.countthemcalories.activities.addingredient.inject.AddIngredientModule;
import com.github.st1hy.countthemcalories.activities.addingredient.inject.DaggerAddIngredientComponent;
import com.github.st1hy.countthemcalories.activities.tags.view.TagsActivity;
import com.github.st1hy.countthemcalories.core.withpicture.view.WithPictureActivity;
import com.jakewharton.rxbinding.view.RxView;

import java.util.Collection;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.subjects.PublishSubject;
import timber.log.Timber;

public class AddIngredientActivity extends WithPictureActivity implements AddIngredientScreen {
    public static final String RESULT_INGREDIENT_ID_LONG = "ingredient result id";

    private static final int REQUEST_PICK_TAG = 0x2010;
    AddIngredientComponent component;

    @BindView(R.id.add_ingredient_toolbar)
    Toolbar toolbar;
    @BindView(R.id.add_ingredient_image)
    ImageView ingredientImage;
    @BindView(R.id.add_ingredient_image_overlay_top)
    View imageOverlayTop;
    @BindView(R.id.add_ingredient_image_overlay_bottom)
    View imageOverlayBottom;

    @Inject
    AddIngredientFragment content;

    final PublishSubject<Void> saveClickedSubject = PublishSubject.create();

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
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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
    public ImageView getImageView() {
        return ingredientImage;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PICK_TAG) {
            if (resultCode == RESULT_OK) {
                long tagId = data.getLongExtra(TagsActivity.EXTRA_TAG_ID, -1);
                String tagName = data.getStringExtra(TagsActivity.EXTRA_TAG_NAME);
                if (tagId != -1 && tagName != null) {
                    content.onNewTagAddedToIngredient(tagId, tagName);
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

    @Override
    public void hideImageOverlay() {
        imageOverlayBottom.setVisibility(View.GONE);
        imageOverlayTop.setVisibility(View.GONE);
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

    @Override
    public void showInWebBrowser(@NonNull Uri address) {
        startActivity(new Intent(Intent.ACTION_VIEW, address));
    }
}
