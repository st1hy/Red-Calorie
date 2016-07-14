package com.github.st1hy.countthemcalories.activities.addingredient.view;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.withpicture.view.WithPictureView;
import com.google.common.base.Optional;

import java.util.Collection;

import rx.Observable;

public interface AddIngredientView extends WithPictureView {
    void setResultAndFinish(@NonNull Intent intent);

    void setSelectedUnitName(@NonNull String unitName);

    void setName(@NonNull String name);

    void setEnergyDensityValue(@NonNull String energyValue);

    /**
     * @param tagNames add to excluded
     */
    void openSelectTagScreen(@NonNull Collection<String> tagNames);

    @NonNull
    Observable<CharSequence> getNameObservable();

    @NonNull
    Observable<CharSequence> getValueObservable();

    void showNameError(@NonNull Optional<Integer> errorResId);

    void showValueError(@NonNull Optional<Integer> errorResId);

    void requestFocusToName();

    void requestFocusToValue();

    @NonNull
    Observable<Void> getSaveObservable();
}
