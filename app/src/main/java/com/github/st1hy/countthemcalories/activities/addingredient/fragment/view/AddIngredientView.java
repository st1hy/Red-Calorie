package com.github.st1hy.countthemcalories.activities.addingredient.fragment.view;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.addingredient.view.AddIngredientScreen;
import com.google.common.base.Optional;

import rx.Observable;

public interface AddIngredientView extends AddIngredientScreen {

    void setSelectedUnitName(@NonNull String unitName);

    void setName(@NonNull String name);

    void setEnergyDensityValue(@NonNull String energyValue);

    @NonNull
    Observable<CharSequence> getNameObservable();

    @NonNull
    Observable<CharSequence> getValueObservable();

    void showNameError(@NonNull Optional<Integer> errorResId);

    void showValueError(@NonNull Optional<Integer> errorResId);

    void requestFocusToName();

    void requestFocusToValue();

    @NonNull
    Observable<Void> getSearchObservable();
}
