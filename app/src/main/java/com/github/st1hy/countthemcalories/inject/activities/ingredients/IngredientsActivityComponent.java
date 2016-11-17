package com.github.st1hy.countthemcalories.inject.activities.ingredients;

import com.github.st1hy.countthemcalories.activities.ingredients.IngredientsActivity;
import com.github.st1hy.countthemcalories.inject.PerActivity;
import com.github.st1hy.countthemcalories.inject.activities.ingredients.fragment.IngredientsFragmentComponentFactory;
import com.github.st1hy.countthemcalories.inject.core.UndoModule;

import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = {
        IngredientsActivityModule.class,
        UndoModule.class
})
public interface IngredientsActivityComponent extends IngredientsFragmentComponentFactory {

    void inject(IngredientsActivity activity);

}
