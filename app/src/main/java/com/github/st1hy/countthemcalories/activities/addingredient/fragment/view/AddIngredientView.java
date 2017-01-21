package com.github.st1hy.countthemcalories.activities.addingredient.fragment.view;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.InputType;
import com.github.st1hy.countthemcalories.activities.addingredient.view.AddIngredientScreen;
import com.github.st1hy.countthemcalories.core.state.Visibility;

import rx.Observable;

public interface AddIngredientView extends AddIngredientScreen {

    void setSelectedUnitName(@NonNull String unitName);

    void setName(@NonNull String name);

    void setEnergyDensityValue(@NonNull String energyValue);

    @NonNull
    @CheckResult
    Observable<CharSequence> getNameObservable();

    @NonNull
    @CheckResult
    Observable<CharSequence> getValueObservable();

    void showError(@NonNull InputType type, @StringRes int errorResId);

    void hideError(@NonNull InputType type);

    void requestFocusTo(@NonNull InputType type);

    @NonNull
    @CheckResult
    Observable<Void> getSearchObservable();

    @NonNull
    @CheckResult
    Observable<Void> getSelectTypeObservable();

    void setNoCategoriesVisibility(@NonNull Visibility visibility);
}
