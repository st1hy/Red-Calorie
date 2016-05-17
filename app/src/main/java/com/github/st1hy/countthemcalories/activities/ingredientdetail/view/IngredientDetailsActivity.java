package com.github.st1hy.countthemcalories.activities.ingredientdetail.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.inject.DaggerIngredientDetailComponent;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.inject.IngredientDetailComponent;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.inject.IngredientDetailsModule;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.presenter.IngredientDetailPresenter;
import com.github.st1hy.countthemcalories.core.baseview.BaseActivity;
import com.github.st1hy.countthemcalories.database.parcel.IngredientTypeParcel;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.math.BigDecimal;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

public class IngredientDetailsActivity extends BaseActivity implements IngredientDetailView {
    public static final String ACTION_EDIT_INGREDIENT = "ingredient details edit action";
    public static final String EXTRA_INGREDIENT_TEMPLATE_PARCEL = "ingredient details extra template parcel";
    public static final String EXTRA_INGREDIENT_AMOUNT_BIGDECIMAL = "ingredient details extra amount";
    public static final String EXTRA_INGREDIENT_ID_LONG = "ingredient details extra id long";
    public static final int RESULT_REMOVE = 0x200;

    @BindView(R.id.add_meal_ingredient_accept)
    ImageButton accept;
    @BindView(R.id.add_meal_ingredient_remove)
    ImageButton remove;
    @BindView(R.id.add_meal_ingredient_edit_amount)
    EditText editAmount;
    @BindView(R.id.add_meal_ingredient_item_name)
    TextView name;
    @BindView(R.id.add_meal_ingredient_energy_density)
    TextView energyDensity;
    @BindView(R.id.add_meal_ingredient_calorie_count)
    TextView calorieCount;
    @BindView(R.id.add_meal_ingredient_image)
    ImageView image;
    @BindView(R.id.add_meal_ingredient_unit) TextView unit;
    @BindView(R.id.add_meal_ingredient_root)
    CardView cardRoot;

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
        Editable text = this.editAmount.getText();
        text.clear();
        text.append(readableAmount);
//        this.editAmount.setText(readableAmount);
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

    @NonNull
    @Override
    public ImageView getImageView() {
        return image;
    }

    @Override
    public void setUnitName(@NonNull String unitName) {
        this.unit.setText(unitName);
    }

    @NonNull
    @Override
    public Observable<Void> getAcceptObservable() {
        return RxView.clicks(accept);
    }

    @NonNull
    @Override
    public Observable<Void> getRemoveObservable() {
        return RxView.clicks(remove);
    }

    @Override
    public void setResultAndFinish(int resultCode, long ingredientId,
                                   @NonNull IngredientTypeParcel parcel,
                                   @NonNull BigDecimal amount) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_INGREDIENT_ID_LONG, ingredientId);
        intent.putExtra(EXTRA_INGREDIENT_TEMPLATE_PARCEL, parcel);
        intent.putExtra(EXTRA_INGREDIENT_AMOUNT_BIGDECIMAL, amount.toPlainString());
        setResult(resultCode, intent);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editAmount.getWindowToken(), 0);
        ActivityCompat.finishAfterTransition(this);
    }

    @NonNull
    @Override
    public String getCurrentAmount() {
        return editAmount.getText().toString();
    }
}
