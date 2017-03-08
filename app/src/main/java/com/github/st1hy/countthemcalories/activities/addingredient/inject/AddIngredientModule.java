package com.github.st1hy.countthemcalories.activities.addingredient.inject;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.AddIngredientFragment;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.inject.AddIngredientFragmentComponentFactory;
import com.github.st1hy.countthemcalories.activities.addingredient.view.AddIngredientMenuAction;
import com.github.st1hy.countthemcalories.activities.addingredient.view.AddIngredientScreen;
import com.github.st1hy.countthemcalories.activities.addingredient.view.AddIngredientScreenImpl;
import com.github.st1hy.countthemcalories.inject.PerActivity;
import com.github.st1hy.countthemcalories.inject.core.ActivityLauncherModule;
import com.github.st1hy.countthemcalories.inject.core.ToolbarNavigateBackModule;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import rx.subjects.PublishSubject;

@Module(includes = {
        ToolbarNavigateBackModule.class,
        ActivityLauncherModule.class,
})
public abstract class AddIngredientModule {
    private static final String CONTENT_TAG = "add ingredient content";

    @Binds
    public abstract AddIngredientScreen addIngredientScreen(AddIngredientScreenImpl screen);

    @Binds
    public abstract AddIngredientFragmentComponentFactory fragmentComponentFactory(
            AddIngredientComponent component);

    @Provides
    public static AddIngredientFragment provideContent(
            FragmentManager fragmentManager,
            AddIngredientFragmentComponentFactory componentFactory) {

        AddIngredientFragment fragment = (AddIngredientFragment) fragmentManager
                .findFragmentByTag(CONTENT_TAG);
        if (fragment == null) {
            fragment = new AddIngredientFragment();

            fragmentManager.beginTransaction()
                    .add(R.id.add_ingredient_content_frame, fragment, CONTENT_TAG)
                    .setTransitionStyle(FragmentTransaction.TRANSIT_NONE)
                    .commitNow();
        }
        fragment.setComponentFactory(componentFactory);
        return fragment;
    }

    @PerActivity
    @Provides
    public static PublishSubject<AddIngredientMenuAction> menuActions() {
        return PublishSubject.create();
    }
}
