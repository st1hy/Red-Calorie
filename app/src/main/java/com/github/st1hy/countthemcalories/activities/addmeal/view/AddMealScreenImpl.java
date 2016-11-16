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
import com.github.st1hy.countthemcalories.inject.activities.ingredientdetail.fragment.IngredientsDetailFragmentModule;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.IngredientDetailActivity;
import com.github.st1hy.countthemcalories.activities.ingredients.IngredientsActivity;
import com.github.st1hy.countthemcalories.activities.overview.OverviewActivity;
import com.github.st1hy.countthemcalories.inject.PerActivity;
import com.github.st1hy.countthemcalories.core.rx.Functions;
import com.github.st1hy.countthemcalories.core.activityresult.ActivityResult;
import com.github.st1hy.countthemcalories.core.activityresult.RxActivityResult;
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
import rx.functions.Func1;

import static android.app.Activity.RESULT_OK;
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
    private final RxActivityResult rxActivityResult;

    @Inject
    public AddMealScreenImpl(@NonNull Activity activity,
                             @NonNull RxActivityResult rxActivityResult) {
        this.activity = activity;
        this.rxActivityResult = rxActivityResult;
        ButterKnife.bind(this, activity);
    }

    @NonNull
    @Override
    public Observable<Void> getAddIngredientButtonObservable() {
        return RxView.clicks(addIngredientFab);
    }

    @Override
    public void onMealSaved() {
        activity.setResult(RESULT_OK);
        Intent intent = new Intent(activity, OverviewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
    }


    @NonNull
    @Override
    public Observable<Ingredient> addIngredient() {
        Intent intent = new Intent(activity, IngredientsActivity.class);
        intent.setAction(IngredientsActivity.ACTION_SELECT_INGREDIENT);
        return rxActivityResult.from(activity)
                .startActivityForResult(intent, REQUEST_PICK_INGREDIENT)
                .filter(ActivityResult.IS_OK)
                .map(new Func1<ActivityResult, IngredientTemplate>() {
                    @Override
                    public IngredientTemplate call(ActivityResult activityResult) {
                        Intent data = activityResult.getData();
                        if (data == null) return null;
                        return Parcels.unwrap(data.getParcelableExtra(IngredientsActivity.EXTRA_INGREDIENT_TYPE_PARCEL));
                    }
                }).filter(Functions.NOT_NULL)
                .map(new Func1<IngredientTemplate, Ingredient>() {
                    @Override
                    public Ingredient call(IngredientTemplate ingredientTemplate) {
                        return new Ingredient(ingredientTemplate, BigDecimal.ZERO);
                    }
                });
    }


    @Override
    @NonNull
    public Observable<IngredientAction> showIngredientDetails(long requestId,
                                                              @NonNull Ingredient ingredient,
                                                              @NonNull List<Pair<View, String>> sharedElements) {
        Bundle startOptions = null;
        if (!sharedElements.isEmpty()) {
            @SuppressWarnings("unchecked")
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                    sharedElements.toArray(new Pair[sharedElements.size()]));
            startOptions = options.toBundle();
        }
        Intent intent = new Intent(activity, IngredientDetailActivity.class);
        intent.putExtra(IngredientsDetailFragmentModule.EXTRA_INGREDIENT_ID_LONG, requestId);
        intent.putExtra(IngredientsDetailFragmentModule.EXTRA_INGREDIENT, Parcels.wrap(ingredient));
        return rxActivityResult.from(activity)
                .startActivityForResult(intent, REQUEST_EDIT_INGREDIENT, startOptions)
                .map(new Func1<ActivityResult, IngredientAction>() {
                    @Override
                    public IngredientAction call(ActivityResult activityResult) {
                        EditIngredientResult result = EditIngredientResult.fromIngredientDetailResult(activityResult.getResultCode());
                        return getIngredientAction(result, activityResult.getData());
                    }
                });
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
                        AddMealScreenImpl.this.ingredientsError = null;
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
