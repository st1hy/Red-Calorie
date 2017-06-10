package com.github.st1hy.countthemcalories.activities.addingredient.inject;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.AddIngredientFragment;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.inject.AddIngredientFragmentComponentFactory;
import com.github.st1hy.countthemcalories.activities.addingredient.view.AddIngredientMenuAction;
import com.github.st1hy.countthemcalories.activities.addingredient.view.AddIngredientScreen;
import com.github.st1hy.countthemcalories.activities.addingredient.view.AddIngredientScreenImpl;
import com.github.st1hy.countthemcalories.core.activityresult.ActivityLauncherModule;
import com.github.st1hy.countthemcalories.core.fragments.FragmentLocation;
import com.github.st1hy.countthemcalories.core.inject.ToolbarNavigateBackModule;
import com.github.st1hy.countthemcalories.core.permissions.PermissionModule;
import com.github.st1hy.countthemcalories.inject.PerActivity;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.Reusable;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;
import rx.subjects.PublishSubject;

@Module(includes = {
        ToolbarNavigateBackModule.class,
        ActivityLauncherModule.class,
        PermissionModule.class,
})
public abstract class AddIngredientModule {
    private static final String CONTENT_TAG = "add ingredient content";

    @Binds
    public abstract AddIngredientScreen addIngredientScreen(AddIngredientScreenImpl screen);

    @Binds
    public abstract AddIngredientFragmentComponentFactory fragmentComponentFactory(
            AddIngredientComponent component);

    @Provides
    @IntoMap
    @StringKey(CONTENT_TAG)
    @Reusable
    public static FragmentLocation addIngredientFragment(AddIngredientFragmentComponentFactory componentFactory) {
        return new FragmentLocation.Builder<>(AddIngredientFragment.class, CONTENT_TAG)
                .setViewRootId(R.id.add_ingredient_content_frame)
                .setInjector(fragment -> fragment.setComponentFactory(componentFactory))
                .build();
    }

    @PerActivity
    @Provides
    public static PublishSubject<AddIngredientMenuAction> menuActions() {
        return PublishSubject.create();
    }
}
