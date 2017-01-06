package com.github.st1hy.countthemcalories.inject.activities.ingredientdetail.fragment;

import com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.presenter.IngredientDetailPresenter;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.presenter.IngredientDetailPresenterImpl;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.view.IngredientDetailView;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.view.IngredientDetailViewImpl;
import com.github.st1hy.countthemcalories.core.headerpicture.imageholder.ImageHolderDelegate;
import com.github.st1hy.countthemcalories.core.headerpicture.imageholder.WithoutPlaceholderImageHolderDelegate;
import com.github.st1hy.countthemcalories.inject.PerFragment;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class IngredientsDetailFragmentBindings {

    @Binds
    @PerFragment
    public abstract IngredientDetailPresenter providePresenter(IngredientDetailPresenterImpl presenter);

    @Binds
    public abstract ImageHolderDelegate provideImageHolderDelegate(
            WithoutPlaceholderImageHolderDelegate holderDelegate);

    @Binds
    public abstract IngredientDetailView provideView(IngredientDetailViewImpl detailView);
}
