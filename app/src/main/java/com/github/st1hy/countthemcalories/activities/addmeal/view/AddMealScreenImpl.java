package com.github.st1hy.countthemcalories.activities.addmeal.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.view.View;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.model.IngredientAction;
import com.github.st1hy.countthemcalories.activities.addmeal.model.EditIngredientResult;
import com.github.st1hy.countthemcalories.activities.addmeal.model.ShowIngredientsInfo;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.IngredientDetailActivity;
import com.github.st1hy.countthemcalories.activities.ingredients.IngredientsActivity;
import com.github.st1hy.countthemcalories.activities.overview.OverviewActivity;
import com.github.st1hy.countthemcalories.core.activityresult.ActivityLauncher;
import com.github.st1hy.countthemcalories.core.activityresult.ActivityResult;
import com.github.st1hy.countthemcalories.core.activityresult.StartParams;
import com.github.st1hy.countthemcalories.core.rx.Functions;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.Meal;
import com.github.st1hy.countthemcalories.inject.PerActivity;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.inject.AddMealFragmentModule;
import com.github.st1hy.countthemcalories.inject.activities.overview.OverviewActivityModule;
import com.jakewharton.rxbinding.view.RxView;

import org.parceler.Parcels;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.functions.Func1;

import static android.app.Activity.RESULT_OK;
import static com.github.st1hy.countthemcalories.activities.addmeal.model.EditIngredientResult.fromIngredientDetailResult;
import static com.github.st1hy.countthemcalories.inject.activities.ingredientdetail.fragment.IngredientsDetailFragmentModule.EXTRA_INGREDIENT;
import static com.github.st1hy.countthemcalories.inject.activities.ingredientdetail.fragment.IngredientsDetailFragmentModule.EXTRA_INGREDIENT_ID_LONG;

@PerActivity
public class AddMealScreenImpl implements AddMealScreen {

    private static final int REQUEST_PICK_INGREDIENT = 0x3903;
    private static final int REQUEST_EDIT_INGREDIENT = 0x3904;

    @NonNull
    private final Activity activity;

    @BindView(R.id.add_meal_fab_add_ingredient)
    FloatingActionButton addIngredientFab;
    @Nullable
    private Snackbar ingredientsError;
    @NonNull
    private final ActivityLauncher activityLauncher;

    @Inject
    public AddMealScreenImpl(@NonNull Activity activity,
                             @NonNull ActivityLauncher activityLauncher) {
        this.activity = activity;
        this.activityLauncher = activityLauncher;
        ButterKnife.bind(this, activity);
    }

    @NonNull
    @Override
    public Observable<Void> getAddIngredientButtonObservable() {
        return RxView.clicks(addIngredientFab);
    }

    @Override
    public void onMealSaved(@NonNull Meal meal) {
        activity.setResult(RESULT_OK);
        Intent intent = new Intent(activity, OverviewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(OverviewActivityModule.EXTRA_JUMP_TO_DATE, meal.getCreationDate());
        activity.startActivity(intent);
    }

    @NonNull
    @Override
    public Observable.Transformer<Void, Ingredient> newIngredients() {
        return voidObservable -> voidObservable.map(
                aVoid -> {
                    Intent intent = new Intent(activity, IngredientsActivity.class);
                    intent.setAction(IngredientsActivity.ACTION_SELECT_INGREDIENT);
                    return StartParams.of(intent, REQUEST_PICK_INGREDIENT);
                })
                .compose(activityLauncher.startActivityForResult(REQUEST_PICK_INGREDIENT))
                .filter(ActivityResult.IS_OK)
                .map(new Func1<ActivityResult, IngredientTemplate>() {
                    @Override
                    public IngredientTemplate call(ActivityResult activityResult) {
                        Intent data = activityResult.getData();
                        if (data == null) return null;
                        return Parcels.unwrap(data.getParcelableExtra(AddMealFragmentModule.EXTRA_INGREDIENT_PARCEL));
                    }
                }).filter(Functions.NOT_NULL)
                .map(ingredientTemplate -> new Ingredient(ingredientTemplate, BigDecimal.ZERO));
    }


    @Override
    @NonNull
    public Observable.Transformer<ShowIngredientsInfo, IngredientAction> showIngredientDetails() {
        return infoObservable -> infoObservable
                .map(info -> {
                    Bundle startOptions = null;
                    List<Pair<View, String>> sharedElements = info.getSharedElements();
                    if (!sharedElements.isEmpty()) {
                        @SuppressWarnings("unchecked")
                        Pair<View, String>[] pairs = sharedElements.toArray(new Pair[sharedElements.size()]);
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, pairs);
                        startOptions = options.toBundle();
                    }
                    Intent intent = new Intent(activity, IngredientDetailActivity.class);
                    intent.putExtra(EXTRA_INGREDIENT_ID_LONG, info.getId());
                    intent.putExtra(EXTRA_INGREDIENT, Parcels.wrap(info.getIngredient()));
                    return StartParams.of(intent, REQUEST_EDIT_INGREDIENT, startOptions);
                })
                .compose(activityLauncher.startActivityForResult(REQUEST_EDIT_INGREDIENT))
                .map(activityResult -> {
                    EditIngredientResult result = fromIngredientDetailResult(
                            activityResult.getResultCode()
                    );
                    return getIngredientAction(result, activityResult.getData());
                });
    }

    @Override
    public void showSnackbarError(@NonNull String error) {
        if (!isSnackbarShown()) {
            ingredientsError = Snackbar.make(addIngredientFab, error, Snackbar.LENGTH_INDEFINITE);
            ingredientsError.addCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    super.onDismissed(snackbar, event);
                    AddMealScreenImpl.this.ingredientsError = null;
                }
            });
            ingredientsError.show();
        }
    }

    @Override
    public void hideSnackbarError() {
        if (isSnackbarShown() && ingredientsError != null) ingredientsError.dismiss();
    }

    private boolean isSnackbarShown() {
        return ingredientsError != null && ingredientsError.isShownOrQueued();
    }

    @NonNull
    private IngredientAction getIngredientAction(@NonNull EditIngredientResult result, @Nullable Intent data) {
        if (data == null) return IngredientAction.CANCELED;
        long requestId = data.getLongExtra(EXTRA_INGREDIENT_ID_LONG, -2L);
        if (requestId == -2L) return IngredientAction.CANCELED;
        IngredientAction ingredientAction;
        switch (result) {
            case REMOVE:
                ingredientAction = IngredientAction.valueOf(IngredientAction.Type.REMOVE, requestId, null);
                break;
            case EDIT:
                Ingredient ingredient = Parcels.unwrap(data.getParcelableExtra(EXTRA_INGREDIENT));
                if (ingredient == null) return IngredientAction.CANCELED;
                ingredientAction = IngredientAction.valueOf(IngredientAction.Type.EDIT, requestId, ingredient);
                break;
            case UNKNOWN:
            default:
                ingredientAction = IngredientAction.CANCELED;
        }
        return ingredientAction;
    }
}
