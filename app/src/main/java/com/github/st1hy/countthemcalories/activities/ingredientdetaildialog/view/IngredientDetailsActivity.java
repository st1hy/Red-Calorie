package com.github.st1hy.countthemcalories.activities.ingredientdetaildialog.view;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.ingredientdetaildialog.inject.DaggerIngredientDetailComponent;
import com.github.st1hy.countthemcalories.activities.ingredientdetaildialog.inject.IngredientDetailComponent;
import com.github.st1hy.countthemcalories.activities.ingredientdetaildialog.inject.IngredientDetailsModule;
import com.github.st1hy.countthemcalories.activities.ingredientdetaildialog.presenter.IngredientDetailPresenter;
import com.github.st1hy.countthemcalories.core.baseview.BaseActivity;
import com.jakewharton.rxbinding.widget.RxTextView;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;

public class IngredientDetailsActivity extends BaseActivity implements IngredientDetailView {
    public static final String ACTION_EDIT_INGREDIENT = "ingredient details edit action";
    public static final String EXTRA_INGREDIENT_TEMPLATE_PARCEL = "ingredient details extra template parcel";
    public static final String EXTRA_INGREDIENT_AMOUNT_BIGDECIMAL = "ingredient details extra amount";

    @Bind(R.id.add_meal_ingredient_accept)
    ImageButton accept;
    @Bind(R.id.add_meal_ingredient_remove)
    ImageButton remove;
    @Bind(R.id.add_meal_ingredient_edit_amount)
    EditText editAmount;
    @Bind(R.id.add_meal_ingredient_item_name)
    TextView name;
    @Bind(R.id.add_meal_ingredient_energy_density)
    TextView energyDensity;
    @Bind(R.id.add_meal_ingredient_calorie_count)
    TextView calorieCount;
    @Bind(R.id.add_meal_ingredient_image)
    ImageView image;

    IngredientDetailComponent component;

    @Inject
    IngredientDetailPresenter presenter;

    @NonNull
    protected IngredientDetailComponent getComponent(@Nullable Bundle savedInstanceState) {
        if (component == null) {
            component = DaggerIngredientDetailComponent.builder()
                    .applicationComponent(getAppComponent())
                    .ingredientDetailsModule(new IngredientDetailsModule(this, savedInstanceState))
                    .build();
        }
        return component;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_meal_ingredient_item_extended);
        ButterKnife.bind(this);
        getComponent(savedInstanceState).inject(this);
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        presenter.onSaveState(outState);
    }

    @Override
    public void setName(@NonNull String name) {
        this.name.setText(name);
    }

    @Override
    public void setEnergyDensity(@NonNull String readableEnergyDensity) {
        this.energyDensity.setText(readableEnergyDensity);
    }

    @Override
    public void setAmount(@NonNull String readableAmount) {
        this.editAmount.setText(readableAmount);
    }

    @Override
    public void setCalorieCount(@NonNull String calorieCount) {
        this.calorieCount.setText(calorieCount);
    }

    @NonNull
    @Override
    public Observable<CharSequence> getAmountObservable() {
        return RxTextView.textChanges(editAmount);
    }

    @Override
    public void setAmountError(@Nullable String errorResId) {
        editAmount.setError(errorResId);
    }
}
