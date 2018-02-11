package com.github.st1hy.countthemcalories.ui.activities.addmeal.inject;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.ui.activities.addmeal.fragment.AddMealFragment;
import com.github.st1hy.countthemcalories.ui.activities.addmeal.view.AddMealMenuAction;
import com.github.st1hy.countthemcalories.ui.activities.addmeal.view.AddMealScreen;
import com.github.st1hy.countthemcalories.ui.activities.addmeal.view.AddMealScreenImpl;
import com.github.st1hy.countthemcalories.ui.core.activityresult.ActivityLauncherModule;
import com.github.st1hy.countthemcalories.ui.core.FragmentLocation;
import com.github.st1hy.countthemcalories.ui.inject.core.ToolbarNavigateBackModule;
import com.github.st1hy.countthemcalories.ui.inject.core.PermissionModule;
import com.github.st1hy.countthemcalories.ui.inject.app.PerActivity;

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
public abstract class AddMealActivityModule {

    private static final String ADD_MEAL_CONTENT = "add meal content";

    @Binds
    public abstract AddMealScreen addMealScreen(AddMealScreenImpl screen);

    @Provides
    @IntoMap
    @StringKey(ADD_MEAL_CONTENT)
    @Reusable
    public static FragmentLocation addMealContent(AddMealActivityComponent component) {
        return new FragmentLocation.Builder<>(AddMealFragment.class, ADD_MEAL_CONTENT)
                .setViewRootId(R.id.add_meal_content_frame)
                .setInjector(fragment -> fragment.setComponentFactory(component))
                .build();
    }

    @Provides
    @PerActivity
    public static PublishSubject<AddMealMenuAction> menuActionPublishSubject() {
        return PublishSubject.create();
    }

}
