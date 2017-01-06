package com.github.st1hy.countthemcalories.inject.activities.mealdetail.fragment;

import com.github.st1hy.countthemcalories.activities.mealdetail.fragment.presenter.MealDetailPresenter;
import com.github.st1hy.countthemcalories.activities.mealdetail.fragment.presenter.MealDetailPresenterImpl;
import com.github.st1hy.countthemcalories.activities.mealdetail.fragment.presenter.MealIngredientsPresenter;
import com.github.st1hy.countthemcalories.activities.mealdetail.fragment.view.MealDetailView;
import com.github.st1hy.countthemcalories.activities.mealdetail.fragment.view.MealDetailViewImpl;
import com.github.st1hy.countthemcalories.core.adapter.delegate.RecyclerAdapterWrapper;
import com.github.st1hy.countthemcalories.core.headerpicture.imageholder.ImageHolderDelegate;
import com.github.st1hy.countthemcalories.core.headerpicture.imageholder.WithoutPlaceholderImageHolderDelegate;
import com.github.st1hy.countthemcalories.inject.PerFragment;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class MealDetailsBindings {

    @Binds
    public abstract MealDetailView provideView(MealDetailViewImpl mealDetailView);

    @Binds
    public abstract MealDetailPresenter providePresenter(MealDetailPresenterImpl presenter);

    @Binds
    @PerFragment
    public abstract ImageHolderDelegate provideImageHolder(WithoutPlaceholderImageHolderDelegate imageHolderDelegate);

    @Binds
    public abstract RecyclerAdapterWrapper recyclerAdapterWrapper(MealIngredientsPresenter presenter);

}
