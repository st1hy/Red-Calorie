package com.github.st1hy.countthemcalories.activities.addingredient.fragment.view;

import android.content.res.Resources;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.InputType;
import com.github.st1hy.countthemcalories.activities.addingredient.view.AddIngredientScreen;
import com.github.st1hy.countthemcalories.activities.addingredient.view.AddIngredientScreenDelegate;
import com.github.st1hy.countthemcalories.core.state.Visibility;
import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.google.common.collect.ImmutableMap;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

@PerFragment
public class AddIngredientViewController extends AddIngredientScreenDelegate implements AddIngredientView {

    @NonNull
    private final Resources resources;
    @NonNull
    private final AddIngredientScreen screen;

    @BindView(R.id.add_ingredient_name)
    EditText name;
    @BindView(R.id.add_ingredient_energy_density)
    EditText energyDensityValue;
    @BindView(R.id.add_ingredient_unit)
    Button energyDensityUnit;
    @BindView(R.id.add_ingredient_name_search)
    View searchName;
    @BindView(R.id.add_ingredient_categories_empty)
    View noCategories;

    private final Map<InputType, EditText> inputMap;

    @Inject
    public AddIngredientViewController(@NonNull View rootView,
                                       @NonNull Resources resources,
                                       @NonNull AddIngredientScreen screen) {
        this.resources = resources;
        this.screen = screen;
        ButterKnife.bind(this, rootView);
        inputMap = ImmutableMap.of(
                InputType.NAME, name,
                InputType.VALUE, energyDensityValue
        );
    }

    @Override
    protected AddIngredientScreen getDelegate() {
        return screen;
    }

    @Override
    public void setSelectedUnitName(@NonNull String unitName) {
        energyDensityUnit.setText(unitName);
    }

    @Override
    public void setName(@NonNull String name) {
        this.name.setText(name);
        this.name.setSelection(name.length());
    }

    @Override
    public void setEnergyDensityValue(@NonNull String energyValue) {
        this.energyDensityValue.setText(energyValue);
        this.energyDensityValue.setSelection(energyValue.length());
    }

    @NonNull
    @Override
    public Observable<CharSequence> getNameObservable() {
        return RxTextView.textChanges(name).skip(1);
    }

    @NonNull
    @Override
    public Observable<CharSequence> getValueObservable() {
        return RxTextView.textChanges(energyDensityValue).skip(1);
    }

    @Override
    public void showError(@NonNull InputType type, @StringRes int errorResId) {
        EditText input = getInputOfType(type);
        String errorString = resources.getString(errorResId);
        input.setError(errorString);
    }

    @Override
    public void hideError(@NonNull InputType type) {
        EditText input = getInputOfType(type);
        input.setError(null);
    }

    @Override
    public void requestFocusTo(@NonNull InputType type) {
        EditText input = getInputOfType(type);
        input.requestFocus();
    }

    @NonNull
    private EditText getInputOfType(@NonNull InputType type) {
        return inputMap.get(type);
    }

    @NonNull
    @Override
    public Observable<Void> getSearchObservable() {
        return RxView.clicks(searchName);
    }

    @NonNull
    @Override
    public Observable<Void> getSelectTypeObservable() {
        return RxView.clicks(energyDensityUnit);
    }

    @Override
    public void setNoCategoriesVisibility(@NonNull Visibility visibility) {
        //noinspection WrongConstant
        noCategories.setVisibility(visibility.getVisibility());
    }

    @Override
    @NonNull
    @CheckResult
    public Observable<Void> addTagObservable() {
        return screen.addTagObservable();
    }
}
