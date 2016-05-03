package com.github.st1hy.countthemcalories.activities.addingredient.view;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.ui.withpicture.view.WithPictureView;

import rx.Observable;

public interface AddIngredientView extends WithPictureView {
    void setResultAndFinish();

    void setSelectedUnitName(@NonNull String unitName);

    void setName(@NonNull String name);

    void setEnergyDensityValue(@NonNull String energyValue);

    void openSelectTagScreen();

    Observable<CharSequence> getNameObservable();

    Observable<CharSequence> getValueObservable();
}
