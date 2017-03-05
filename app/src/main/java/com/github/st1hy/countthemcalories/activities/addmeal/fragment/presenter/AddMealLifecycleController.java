package com.github.st1hy.countthemcalories.activities.addmeal.fragment.presenter;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.BasicLifecycle;
import com.github.st1hy.countthemcalories.core.headerpicture.SelectPicturePresenter;
import com.github.st1hy.countthemcalories.inject.PerFragment;

import javax.inject.Inject;

@PerFragment
public class AddMealLifecycleController implements BasicLifecycle {

    @NonNull
    private final AddMealPresenter mealPresenter;
    @NonNull
    private final SelectPicturePresenter picturePresenter;

    @Inject
    public AddMealLifecycleController(@NonNull AddMealPresenter mealPresenter,
                                      @NonNull SelectPicturePresenter picturePresenter) {
        this.mealPresenter = mealPresenter;
        this.picturePresenter = picturePresenter;
    }


    @Override
    public void onStart() {
        mealPresenter.onStart();
        picturePresenter.onStart();
    }

    @Override
    public void onStop() {
        mealPresenter.onStop();
        picturePresenter.onStop();
    }
}
