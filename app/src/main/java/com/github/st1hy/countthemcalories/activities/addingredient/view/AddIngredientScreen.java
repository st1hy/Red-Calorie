package com.github.st1hy.countthemcalories.activities.addingredient.view;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.withpicture.view.WithPictureView;

import java.util.Collection;

import rx.Observable;

public interface AddIngredientScreen extends WithPictureView {

    void setResultAndFinish(@NonNull Intent intent);

    /**
     * @param tagNames add to be excluded
     */
    void openSelectTagScreen(@NonNull Collection<String> tagNames);

    @NonNull
    Observable<Void> getSaveObservable();
}
