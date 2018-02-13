package com.github.st1hy.countthemcalories.ui.activities.ingredients.view;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.ui.activities.addingredient.AddIngredientActivity;
import com.github.st1hy.countthemcalories.ui.activities.addingredient.EditIngredientActivity;
import com.github.st1hy.countthemcalories.ui.activities.addingredient.fragment.model.AddIngredientType;
import com.github.st1hy.countthemcalories.ui.activities.addmeal.AddMealActivity;
import com.github.st1hy.countthemcalories.ui.activities.ingredients.model.AddIngredientParams;
import com.github.st1hy.countthemcalories.ui.contract.IngredientTemplate;
import com.github.st1hy.countthemcalories.ui.core.activityresult.ActivityLauncher;
import com.github.st1hy.countthemcalories.ui.core.activityresult.ActivityResult;
import com.github.st1hy.countthemcalories.ui.core.activityresult.StartParams;
import com.github.st1hy.countthemcalories.ui.core.rx.Functions;
import com.github.st1hy.countthemcalories.ui.inject.app.PerActivity;
import com.jakewharton.rxbinding.view.RxView;

import org.parceler.Parcels;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.functions.Func1;

import static android.app.Activity.RESULT_OK;
import static com.github.st1hy.countthemcalories.ui.activities.ingredients.IngredientsActivity.REQUEST_ADD_INGREDIENT;
import static com.github.st1hy.countthemcalories.ui.activities.ingredients.IngredientsActivity.REQUEST_EDIT;

@PerActivity
public class IngredientsScreenImpl implements IngredientsScreen {

    @NonNull
    private final Activity activity;
    @NonNull
    private final ActivityLauncher activityLauncher;

    @BindView(R.id.ingredients_fab)
    FloatingActionButton fab;

    @Inject
    public IngredientsScreenImpl(@NonNull Activity activity,
                                 @NonNull ActivityLauncher activityLauncher) {
        this.activity = activity;
        this.activityLauncher = activityLauncher;
        ButterKnife.bind(this, activity);
    }

    @Override
    @NonNull
    @CheckResult
    public Observable.Transformer<AddIngredientParams, IngredientTemplate> addNewIngredient() {
        return paramsObservable -> paramsObservable
                .map(addIngredientParams -> {
                    AddIngredientType type = addIngredientParams.getType();
                    Intent intent = new Intent(activity, AddIngredientActivity.class);
                    intent.setAction(type.getAction());
                    intent.putExtra(AddIngredientActivity.ARG_EXTRA_NAME,
                            addIngredientParams.getExtraName());
                    return StartParams.of(intent, REQUEST_ADD_INGREDIENT);
                })
                .compose(activityLauncher.startActivityForResult(REQUEST_ADD_INGREDIENT))
                .filter(ActivityResult.IS_OK)
                .map((Func1<ActivityResult, IngredientTemplate>) activityResult -> {
                    Intent data = activityResult.getData();
                    if (data == null) return null;
                    return Parcels.unwrap(data.getParcelableExtra(
                            AddIngredientActivity.RESULT_INGREDIENT_TEMPLATE));
                })
                .filter(Functions.NOT_NULL);
    }

    @NonNull
    @Override
    public Observable<Void> getOnAddIngredientClickedObservable() {
        return RxView.clicks(fab);
    }

    @Override
    public void onIngredientSelected(@NonNull IngredientTemplate ingredientTemplate) {
        Intent result = new Intent();
        result.putExtra(AddMealActivity.EXTRA_INGREDIENT_PARCEL, Parcels.wrap(ingredientTemplate));
        activity.setResult(RESULT_OK, result);
        activity.finish();
    }

    @Override
    public void addToNewMeal(@NonNull IngredientTemplate ingredientTemplate) {
        Intent intent = new Intent(activity, AddMealActivity.class);
        intent.putExtra(AddMealActivity.EXTRA_INGREDIENT_PARCEL, Parcels.wrap(ingredientTemplate));
        activity.startActivity(intent);
    }


    @Override
    public void editIngredientTemplate(long requestID, @NonNull IngredientTemplate ingredientTemplate) {
        Intent intent = new Intent(activity, EditIngredientActivity.class);
        intent.putExtra(AddIngredientActivity.ARG_EDIT_REQUEST_ID_LONG, requestID);
        intent.putExtra(AddIngredientActivity.ARG_EDIT_INGREDIENT_PARCEL, Parcels.wrap(ingredientTemplate));
        activity.startActivityForResult(intent, REQUEST_EDIT);
    }

}
