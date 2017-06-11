package com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.view;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.view.IngredientDetailScreen;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.view.IngredientDetailScreenDelegate;
import com.github.st1hy.countthemcalories.core.rx.Functions;
import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.github.st1hy.countthemcalories.inject.quantifier.view.FragmentRootView;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

@PerFragment
public class IngredientDetailViewImpl extends IngredientDetailScreenDelegate implements IngredientDetailView {

    @NonNull
    private final InputMethodManager inputMethodManager;

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

    @Inject
    public IngredientDetailViewImpl(@NonNull @FragmentRootView View rootView,
                                    @NonNull IngredientDetailScreen screen,
                                    @NonNull InputMethodManager inputMethodManager) {
        super(screen);
        this.inputMethodManager = inputMethodManager;
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
        inputMethodManager.hideSoftInputFromWindow(editAmount.getWindowToken(), 0);
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
        return RxTextView.editorActionEvents(editAmount)
                .filter(event -> {
                    KeyEvent keyEvent = event.keyEvent();
                    return event.actionId() == EditorInfo.IME_ACTION_DONE ||
                            (keyEvent != null && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER);
                }).map(Functions.INTO_VOID)
                .mergeWith(RxView.clicks(accept));
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
