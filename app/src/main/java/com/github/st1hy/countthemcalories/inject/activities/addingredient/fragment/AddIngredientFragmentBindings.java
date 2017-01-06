package com.github.st1hy.countthemcalories.inject.activities.addingredient.fragment;

import com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.AddIngredientModel;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.presenter.AddIngredientPresenter;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.presenter.AddIngredientPresenterImp;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.presenter.IngredientTagsPresenter;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.view.AddIngredientView;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.view.AddIngredientViewController;
import com.github.st1hy.countthemcalories.core.adapter.delegate.RecyclerAdapterWrapper;
import com.github.st1hy.countthemcalories.core.headerpicture.PictureModel;
import com.github.st1hy.countthemcalories.core.headerpicture.SelectPicturePresenter;
import com.github.st1hy.countthemcalories.core.headerpicture.SelectPicturePresenterImp;
import com.github.st1hy.countthemcalories.inject.PerFragment;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class AddIngredientFragmentBindings {

    @Binds
    public abstract AddIngredientView provideView(AddIngredientViewController controller);

    @Binds
    public abstract AddIngredientPresenter providePresenter(AddIngredientPresenterImp presenter);

    @Binds
    @PerFragment
    public abstract SelectPicturePresenter picturePresenter(SelectPicturePresenterImp presenter);

    @Binds
    public abstract RecyclerAdapterWrapper recyclerAdapterWrapper(IngredientTagsPresenter tagsPresenter);

    @Binds
    public abstract PictureModel pictureModel(AddIngredientModel model);
}
