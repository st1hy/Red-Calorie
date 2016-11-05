package com.github.st1hy.countthemcalories.activities.addmeal.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.model.IngredientAction;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.model.IngredientAction.Type;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.view.AddMealFragment;
import com.github.st1hy.countthemcalories.activities.addmeal.inject.AddMealActivityComponent;
import com.github.st1hy.countthemcalories.activities.addmeal.inject.AddMealActivityModule;
import com.github.st1hy.countthemcalories.activities.addmeal.inject.DaggerAddMealActivityComponent;
import com.github.st1hy.countthemcalories.activities.addmeal.model.EditIngredientResult;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.inject.IngredientsDetailFragmentModule;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.view.IngredientDetailActivity;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsActivity;
import com.github.st1hy.countthemcalories.activities.overview.view.OverviewActivity;
import com.github.st1hy.countthemcalories.core.Utils;
import com.github.st1hy.countthemcalories.core.rx.QueueSubject;
import com.github.st1hy.countthemcalories.core.picture.view.WithPictureActivity;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.google.common.base.Optional;
import com.jakewharton.rxbinding.view.RxView;

import org.parceler.Parcels;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;
import timber.log.Timber;

import static com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.inject.IngredientsDetailFragmentModule.EXTRA_INGREDIENT;
import static com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.inject.IngredientsDetailFragmentModule.EXTRA_INGREDIENT_ID_LONG;

public class AddMealActivity extends WithPictureActivity implements AddMealScreen {

    public static final int REQUEST_PICK_INGREDIENT = 0x3903;
    public static final int REQUEST_EDIT_INGREDIENT = 0x3904;

    @BindView(R.id.add_meal_toolbar)
    Toolbar toolbar;
    @BindView(R.id.add_meal_image)
    ImageView mealImage;
    @BindView(R.id.add_meal_fab_add_ingredient)
    FloatingActionButton addIngredientFab;
    @BindView(R.id.add_meal_image_overlay_top)
    View imageOverlayTop;
    @BindView(R.id.add_meal_image_overlay_bottom)
    View imageOverlayBottom;

    AddMealActivityComponent component;

    @Nullable
    Snackbar ingredientsError;

    @Inject
    AddMealFragment content;

    final PublishSubject<Void> saveClickedSubject = PublishSubject.create();
    final Subject<IngredientAction, IngredientAction> ingredientActionSubject = QueueSubject.create();
    final Observable<IngredientAction> ingredientActionObservable = ingredientActionSubject.asObservable();

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
        Utils.assertNotNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @NonNull
    @Override
    public Observable<Void> getSelectPictureObservable() {
        return RxView.clicks(mealImage);
    }

    @NonNull
    @Override
    public Observable<Void> getAddIngredientObservable() {
        return RxView.clicks(addIngredientFab);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_meal_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuActionId = item.getItemId();
        if (menuActionId == R.id.action_save) {
            saveClickedSubject.onNext(null);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void openAddIngredient() {
        Intent intent = new Intent(this, IngredientsActivity.class);
        intent.setAction(IngredientsActivity.ACTION_SELECT_INGREDIENT);
        startActivityForResult(intent, REQUEST_PICK_INGREDIENT);
    }

    @NonNull
    @Override
    public ImageView getImageView() {
        return mealImage;
    }

    @NonNull
    @Override
    public Observable<IngredientAction> getIngredientActionObservable() {
        return ingredientActionObservable;
    }

    @Override
    public final void showIngredientDetails(long requestId,
                                            @NonNull Ingredient ingredient,
                                            @NonNull List<Pair<View, String>> sharedElements) {
        Bundle startOptions = null;
        if (!sharedElements.isEmpty()) {
            @SuppressWarnings("unchecked")
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                    sharedElements.toArray(new Pair[sharedElements.size()]));
            startOptions = options.toBundle();
        }
        Intent intent = new Intent(this, IngredientDetailActivity.class);
        intent.putExtra(IngredientsDetailFragmentModule.EXTRA_INGREDIENT_ID_LONG, requestId);
        intent.putExtra(IngredientsDetailFragmentModule.EXTRA_INGREDIENT, Parcels.wrap(ingredient));
        startActivityForResult(intent, REQUEST_EDIT_INGREDIENT, startOptions);
    }

    @Override
    public void showSnackbarError(@NonNull Optional<String> ingredientsError) {
        boolean isShown = this.ingredientsError != null && this.ingredientsError.isShownOrQueued();
        if (ingredientsError.isPresent()) {
            if (!isShown) {
                this.ingredientsError = Snackbar.make(addIngredientFab, ingredientsError.get(), Snackbar.LENGTH_INDEFINITE);
                this.ingredientsError.setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        super.onDismissed(snackbar, event);
                        AddMealActivity.this.ingredientsError = null;
                    }
                });
                this.ingredientsError.show();
            }
        } else {
            if (isShown) {
                this.ingredientsError.dismiss();
            }
        }
    }

    @NonNull
    @Override
    public Observable<Void> getSaveClickedObservable() {
        return saveClickedSubject.asObservable();
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

    @Override
    public void onMealSaved() {
        setResult(RESULT_OK);
        Intent intent = new Intent(this, OverviewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private boolean handleActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AddMealActivity.REQUEST_PICK_INGREDIENT) {
            if (!handlePickIngredientResult(resultCode, data)) {
                Timber.w("Pick ingredient returned incomplete result: %d, intent: %s", resultCode, data);
            }
            return true;
        } else if (requestCode == AddMealActivity.REQUEST_EDIT_INGREDIENT) {
            EditIngredientResult result = EditIngredientResult.fromIngredientDetailResult(resultCode);
            ingredientActionSubject.onNext(getIngredientAction(result, data));
            return true;
        } else return false;
    }

    private boolean handlePickIngredientResult(int resultCode, @Nullable Intent data) {
        if (resultCode == IngredientsActivity.RESULT_OK) {
            if (data == null) return false;
            IngredientTemplate ingredientTemplate = Parcels.unwrap(data.getParcelableExtra(IngredientsActivity.EXTRA_INGREDIENT_TYPE_PARCEL));
            if (ingredientTemplate == null) return false;
            ingredientActionSubject.onNext(IngredientAction.valueOf(Type.NEW, -1L, new Ingredient(ingredientTemplate, BigDecimal.ZERO)));
        }
        return true;
    }

    @NonNull
    private IngredientAction getIngredientAction(@NonNull EditIngredientResult result, @Nullable Intent data) {
        if (data == null) return IngredientAction.CANCELED;
        long requestId = data.getLongExtra(EXTRA_INGREDIENT_ID_LONG, -2L);
        if (requestId == -2L) return IngredientAction.CANCELED;
        IngredientAction ingredientAction;
        switch (result) {
            case REMOVE:
                ingredientAction = IngredientAction.valueOf(Type.REMOVE, requestId, null);
                break;
            case EDIT:
                Ingredient ingredient = Parcels.unwrap(data.getParcelableExtra(EXTRA_INGREDIENT));
                if (ingredient == null) return IngredientAction.CANCELED;
                ingredientAction = IngredientAction.valueOf(Type.EDIT, requestId,ingredient);
                break;
            case UNKNOWN:
            default:
                ingredientAction = IngredientAction.CANCELED;
        }
        return ingredientAction;
    }
}
