package com.github.st1hy.countthemcalories.activities.ingredients.view;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.AddIngredientType;
import com.github.st1hy.countthemcalories.activities.addingredient.inject.AddIngredientModule;
import com.github.st1hy.countthemcalories.activities.addingredient.view.AddIngredientActivity;
import com.github.st1hy.countthemcalories.activities.addingredient.view.EditIngredientActivity;
import com.github.st1hy.countthemcalories.activities.addingredient.view.SelectIngredientTypeActivity;
import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealActivity;
import com.github.st1hy.countthemcalories.core.rx.Functions;
import com.github.st1hy.countthemcalories.core.rx.activityresult.ActivityResult;
import com.github.st1hy.countthemcalories.core.rx.activityresult.RxActivityResult;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.jakewharton.rxbinding.view.RxView;

import org.parceler.Parcels;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.functions.Func1;

import static android.app.Activity.RESULT_OK;
import static com.github.st1hy.countthemcalories.activities.addingredient.view.AddIngredientActivity.ARG_EDIT_INGREDIENT_PARCEL;
import static com.github.st1hy.countthemcalories.activities.addingredient.view.AddIngredientActivity.ARG_EDIT_REQUEST_ID_LONG;
import static com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsActivity.EXTRA_INGREDIENT_TYPE_PARCEL;
import static com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsActivity.REQUEST_ADD_INGREDIENT;
import static com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsActivity.REQUEST_EDIT;
import static com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsActivity.REQUEST_SELECT_TYPE;

public class IngredientsScreenImpl implements IngredientsScreen {

    @NonNull
    private final Activity activity;
    @NonNull
    private final RxActivityResult rxActivityResult;

    @BindView(R.id.ingredients_fab)
    FloatingActionButton fab;

    @Inject
    public IngredientsScreenImpl(@NonNull Activity activity, @NonNull RxActivityResult rxActivityResult) {
        this.activity = activity;
        this.rxActivityResult = rxActivityResult;
        ButterKnife.bind(this, activity);
    }

    @Override
    @NonNull
    @CheckResult
    public Observable<IngredientTemplate> addNewIngredient(@NonNull AddIngredientType type, @NonNull String extraName) {
        Intent intent = new Intent(activity, AddIngredientActivity.class);
        intent.setAction(type.getAction());
        intent.putExtra(AddIngredientModule.EXTRA_NAME, extraName);
        return rxActivityResult.from(activity)
                .startActivityForResult(intent, REQUEST_ADD_INGREDIENT)
                .filter(ActivityResult.IS_OK)
                .map(new Func1<ActivityResult, IngredientTemplate>() {
                    @Override
                    public IngredientTemplate call(ActivityResult activityResult) {
                        Intent data = activityResult.getData();
                        if (data == null) return null;
                        return Parcels.unwrap(data.getParcelableExtra(AddIngredientActivity.RESULT_INGREDIENT_TEMPLATE));
                    }
                }).filter(Functions.NOT_NULL);
    }

    @NonNull
    @Override
    public Observable<Void> getOnAddIngredientClickedObservable() {
        return RxView.clicks(fab);
    }

    @Override
    public void onIngredientSelected(@NonNull IngredientTemplate ingredientTemplate) {
        Intent result = new Intent();
        result.putExtra(EXTRA_INGREDIENT_TYPE_PARCEL, Parcels.wrap(ingredientTemplate));
        activity.setResult(RESULT_OK, result);
        activity.finish();
    }

    @Override
    public void addToNewMeal(@NonNull IngredientTemplate ingredientTemplate) {
        Intent intent = new Intent(activity, AddMealActivity.class);
        intent.putExtra(EXTRA_INGREDIENT_TYPE_PARCEL, Parcels.wrap(ingredientTemplate));
        activity.startActivity(intent);
    }

    @Override
    @NonNull
    @CheckResult
    public Observable<AddIngredientType> selectIngredientType() {
        Intent intent = new Intent(activity, SelectIngredientTypeActivity.class);
        return rxActivityResult.from(activity)
                .startActivityForResult(intent, REQUEST_SELECT_TYPE)
                .map(new Func1<ActivityResult, AddIngredientType>() {
                    @Override
                    public AddIngredientType call(ActivityResult activityResult) {
                        switch (activityResult.getResultCode()) {
                            case SelectIngredientTypeActivity.RESULT_DRINK:
                                return AddIngredientType.DRINK;
                            case SelectIngredientTypeActivity.RESULT_MEAL:
                                return AddIngredientType.MEAL;
                            default:
                                return null;
                        }
                    }
                }).filter(Functions.NOT_NULL);
    }

    @Override
    public void editIngredientTemplate(long requestID, @NonNull IngredientTemplate ingredientTemplate) {
        Intent intent = new Intent(activity, EditIngredientActivity.class);
        intent.putExtra(ARG_EDIT_REQUEST_ID_LONG, requestID);
        intent.putExtra(ARG_EDIT_INGREDIENT_PARCEL, Parcels.wrap(ingredientTemplate));
        activity.startActivityForResult(intent, REQUEST_EDIT);
    }

}
