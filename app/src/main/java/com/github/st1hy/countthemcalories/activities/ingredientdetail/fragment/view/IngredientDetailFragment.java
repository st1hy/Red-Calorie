package com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.inject.DaggerIngredientDetailFragmentComponent;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.inject.IngredientDetailFragmentComponent;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.inject.IngredientsDetailFragmentModule;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.presenter.IngredientDetailPresenter;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.view.IngredientDetailScreen;
import com.github.st1hy.countthemcalories.core.baseview.BaseFragment;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.google.common.base.Preconditions;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

public class IngredientDetailFragment extends BaseFragment implements IngredientDetailView {

    IngredientDetailFragmentComponent component;

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
    IngredientDetailPresenter presenter;
    @Inject
    IngredientDetailScreen screen;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ingredient_detail_content, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ButterKnife.bind(this, Preconditions.checkNotNull(getView()));
        getComponent(savedInstanceState).inject(this);
    }

    protected IngredientDetailFragmentComponent getComponent(@Nullable Bundle savedState) {
        if (component == null) {
            component = DaggerIngredientDetailFragmentComponent.builder()
                    .applicationComponent(getAppComponent())
                    .ingredientsDetailFragmentModule(new IngredientsDetailFragmentModule(this, savedState))
                    .build();
        }
        return component;
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.onStart();
    }

    @Override
    public void onStop() {
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
        InputMethodManager imm = (InputMethodManager) getActivity()
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

    @Override
    public void commitEditedIngredientChanges(long ingredientId,
                                              @NonNull Ingredient ingredient) {
        screen.commitEditedIngredientChanges(ingredientId, ingredient);
    }

    @Override
    public void removeIngredient(long ingredientId) {
        screen.removeIngredient(ingredientId);
    }

}
