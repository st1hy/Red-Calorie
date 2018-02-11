package com.github.st1hy.countthemcalories.ui.activities.addingredient.fragment.presenter;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.ui.core.BasicLifecycle;
import com.github.st1hy.countthemcalories.ui.core.headerpicture.SelectPicturePresenter;
import com.github.st1hy.countthemcalories.ui.inject.app.PerFragment;

import javax.inject.Inject;

@PerFragment
public class AddIngredientLifecycleController implements BasicLifecycle {

    @NonNull
    private final AddIngredientPresenter presenter;
    @NonNull
    private final SelectPicturePresenter picturePresenter;

    @Inject
    AddIngredientLifecycleController(@NonNull AddIngredientPresenter presenter,
                                     @NonNull SelectPicturePresenter picturePresenter) {
        this.presenter = presenter;
        this.picturePresenter = picturePresenter;
    }

    @Override
    public void onStart() {
        presenter.onStart();
        picturePresenter.onStart();
    }

    @Override
    public void onStop() {
        presenter.onStop();
        picturePresenter.onStop();
    }
}
