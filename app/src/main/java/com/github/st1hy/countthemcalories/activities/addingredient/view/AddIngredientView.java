package com.github.st1hy.countthemcalories.activities.addingredient.view;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.withpicture.view.WithPictureView;
import com.google.common.base.Optional;

import rx.Observable;

public interface AddIngredientView extends WithPictureView {
    void setResultAndFinish();

    void setSelectedUnitName(@NonNull String unitName);

    void setName(@NonNull String name);

    void setEnergyDensityValue(@NonNull String energyValue);

    void openSelectTagScreen();

    @NonNull
    Observable<CharSequence> getNameObservable();

    @NonNull
    Observable<CharSequence> getValueObservable();

    void showNameError(@NonNull Optional<Integer> errorResId);

    void showValueError(@NonNull Optional<Integer> errorResId);
}
