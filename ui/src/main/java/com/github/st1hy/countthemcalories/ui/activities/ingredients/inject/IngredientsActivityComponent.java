package com.github.st1hy.countthemcalories.ui.activities.ingredients.inject;

import com.github.st1hy.countthemcalories.ui.activities.ingredients.IngredientsActivity;
import com.github.st1hy.countthemcalories.ui.activities.ingredients.fragment.inject.IngredientsFragmentComponentFactory;
import com.github.st1hy.countthemcalories.ui.inject.app.PerActivity;
import com.github.st1hy.countthemcalories.ui.inject.core.ActivityModule;

import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = {
        ActivityModule.class,
        IngredientsActivityModule.class,
})
public interface IngredientsActivityComponent extends IngredientsFragmentComponentFactory {

    void inject(IngredientsActivity activity);

}
