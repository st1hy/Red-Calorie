package com.github.st1hy.countthemcalories.inject.activities.addmeal.fragment;

import com.github.st1hy.countthemcalories.activities.addmeal.fragment.model.AddMealModel;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.presenter.AddMealPresenter;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.presenter.AddMealPresenterImp;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.presenter.IngredientsListPresenter;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.view.AddMealView;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.view.AddMealViewController;
import com.github.st1hy.countthemcalories.core.adapter.delegate.RecyclerAdapterWrapper;
import com.github.st1hy.countthemcalories.core.headerpicture.PictureModel;
import com.github.st1hy.countthemcalories.core.headerpicture.SelectPicturePresenter;
import com.github.st1hy.countthemcalories.core.headerpicture.SelectPicturePresenterImp;
import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.github.st1hy.countthemcalories.inject.activities.addmeal.fragment.ingredientitems.IngredientListComponentFactory;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class AddMealFragmentBindings {

    @Binds
    public abstract AddMealPresenter providePresenter(AddMealPresenterImp presenter);
    
    @Binds
    public abstract AddMealView provideView(AddMealViewController addMealViewController);

    @Binds
    public abstract RecyclerAdapterWrapper ingredientsAdapter(IngredientsListPresenter listPresenter);

    @Binds
    @PerFragment
    public abstract SelectPicturePresenter picturePresenter(SelectPicturePresenterImp presenter);

    @Binds
    public abstract PictureModel pictureModel(AddMealModel model);

    @Binds
    public abstract IngredientListComponentFactory ingredientListComponentFactory(
            AddMealFragmentComponent component);
}
