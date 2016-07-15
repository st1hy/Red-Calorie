package com.github.st1hy.countthemcalories.activities.addmeal.fragment.view;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealScreen;
import com.github.st1hy.countthemcalories.core.state.Visibility;
import com.github.st1hy.countthemcalories.core.withpicture.view.WithPictureView;
import com.google.common.base.Optional;

import rx.Observable;

public interface AddMealView extends WithPictureView, AddMealScreen {

    void onMealSaved();

    void setName(@NonNull String name);

    @NonNull
    Observable<CharSequence> getNameObservable();

    void setEmptyIngredientsVisibility(@NonNull Visibility visibility);

    void scrollTo(int itemPosition);

    void setTotalEnergy(@NonNull String totalEnergy);

    void showNameError(@NonNull Optional<String> error);

}
