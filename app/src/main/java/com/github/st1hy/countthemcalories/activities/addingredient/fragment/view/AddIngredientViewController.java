package com.github.st1hy.countthemcalories.activities.addingredient.fragment.view;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addingredient.view.AddIngredientScreen;
import com.github.st1hy.countthemcalories.activities.addingredient.view.AddIngredientScreenDelegate;
import com.google.common.base.Optional;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

public class AddIngredientViewController extends AddIngredientScreenDelegate implements AddIngredientView {

    @NonNull private final Resources resources;
    @NonNull private final AddIngredientScreen screen;

    @BindView(R.id.add_ingredient_name)
    EditText name;
    @BindView(R.id.add_ingredient_energy_density)
    EditText energyDensityValue;
    @BindView(R.id.add_ingredient_unit)
    Button energyDensityUnit;
    @BindView(R.id.add_ingredient_name_search)
    View searchName;

    @Inject
    public AddIngredientViewController(@NonNull View rootView,
                                       @NonNull Resources resources,
                                       @NonNull AddIngredientScreen screen) {
        this.resources = resources;
        ButterKnife.bind(this, rootView);
        this.screen = screen;
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
    }

    @Override
    public void setEnergyDensityValue(@NonNull String energyValue) {
        this.energyDensityValue.setText(energyValue);
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
    public void showNameError(@NonNull Optional<Integer> errorResId) {
        if (errorResId.isPresent()) {
            name.setError(resources.getString(errorResId.get()));
        } else {
            name.setError(null);
        }
    }

    @Override
    public void showValueError(@NonNull Optional<Integer> errorResId) {
        if (errorResId.isPresent()) {
            energyDensityValue.setError(resources.getString(errorResId.get()));
        } else {
            energyDensityValue.setError(null);
        }
    }

    @Override
    public void requestFocusToName() {
        name.requestFocus();
    }

    @Override
    public void requestFocusToValue() {
        energyDensityValue.requestFocus();
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
}
