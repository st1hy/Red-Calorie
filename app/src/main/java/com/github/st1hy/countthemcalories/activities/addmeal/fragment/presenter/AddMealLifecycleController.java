package com.github.st1hy.countthemcalories.activities.addmeal.fragment.presenter;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.BasicLifecycle;
import com.github.st1hy.countthemcalories.core.adapter.delegate.RecyclerViewAdapterDelegate;
import com.github.st1hy.countthemcalories.core.headerpicture.SelectPicturePresenter;
import com.github.st1hy.countthemcalories.inject.PerFragment;

import javax.inject.Inject;

@PerFragment
public class AddMealLifecycleController implements BasicLifecycle {

    @NonNull
    private final IngredientsListPresenter listPresenter;
    @NonNull
    private final AddMealPresenter mealPresenter;
    @NonNull
    private final RecyclerViewAdapterDelegate adapterDelegate;
    @NonNull
    private final SelectPicturePresenter picturePresenter;

    @Inject
    public AddMealLifecycleController(@NonNull IngredientsListPresenter listPresenter,
                                      @NonNull AddMealPresenter mealPresenter,
                                      @NonNull RecyclerViewAdapterDelegate adapterDelegate,
                                      @NonNull SelectPicturePresenter picturePresenter) {
        this.listPresenter = listPresenter;
        this.mealPresenter = mealPresenter;
        this.adapterDelegate = adapterDelegate;
        this.picturePresenter = picturePresenter;
    }


    @Override
    public void onStart() {
        mealPresenter.onStart();
        adapterDelegate.onStart();
        listPresenter.onStart();
        picturePresenter.onStart();
    }

    @Override
    public void onStop() {
        mealPresenter.onStop();
        adapterDelegate.onStop();
        listPresenter.onStop();
        picturePresenter.onStop();
    }
}
