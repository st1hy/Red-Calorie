package com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.view.IngredientDetailScreen;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.view.IngredientDetailScreenDelegate;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

public class IngredientDetailViewImpl extends IngredientDetailScreenDelegate implements IngredientDetailView {

    @NonNull private final Context context;

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
    @BindView(R.id.add_meal_ingredient_unit)
    TextView unit;

    public IngredientDetailViewImpl(@NonNull View rootView,
                                    @NonNull IngredientDetailScreen screen,
                                    @NonNull Context context) {
        super(screen);
        this.context = context;
        ButterKnife.bind(this, rootView);
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

    @Override
    public void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editAmount.getWindowToken(), 0);
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

    @NonNull
    @Override
    public String getCurrentAmount() {
        return editAmount.getText().toString();
    }
}
