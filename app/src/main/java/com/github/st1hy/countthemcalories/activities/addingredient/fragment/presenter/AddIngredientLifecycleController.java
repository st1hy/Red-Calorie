package com.github.st1hy.countthemcalories.activities.addingredient.fragment.presenter;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.BasicLifecycle;
import com.github.st1hy.countthemcalories.core.adapter.delegate.RecyclerViewAdapterDelegate;
import com.github.st1hy.countthemcalories.core.headerpicture.SelectPicturePresenter;
import com.github.st1hy.countthemcalories.inject.PerFragment;

import javax.inject.Inject;

@PerFragment
public class AddIngredientLifecycleController implements BasicLifecycle {

    @NonNull
    private final RecyclerViewAdapterDelegate adapterDelegate;
    @NonNull
    private final AddIngredientPresenter presenter;
    @NonNull
    private final SelectPicturePresenter picturePresenter;

    @Inject
    public AddIngredientLifecycleController(@NonNull RecyclerViewAdapterDelegate adapterDelegate,
                                            @NonNull AddIngredientPresenter presenter,
                                            @NonNull SelectPicturePresenter picturePresenter) {
        this.adapterDelegate = adapterDelegate;
        this.presenter = presenter;
        this.picturePresenter = picturePresenter;
    }

    @Override
    public void onStart() {
        adapterDelegate.onStart();
        presenter.onStart();
        picturePresenter.onStart();
    }

    @Override
    public void onStop() {
        adapterDelegate.onStop();
        presenter.onStop();
        picturePresenter.onStop();
    }
}
