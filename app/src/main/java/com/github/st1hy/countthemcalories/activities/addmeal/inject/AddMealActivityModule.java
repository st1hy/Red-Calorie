package com.github.st1hy.countthemcalories.activities.addmeal.inject;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.AddMealFragment;
import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealMenuAction;
import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealScreen;
import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealScreenImpl;
import com.github.st1hy.countthemcalories.inject.PerActivity;
import com.github.st1hy.countthemcalories.core.activityresult.ActivityLauncherModule;
import com.github.st1hy.countthemcalories.core.inject.ToolbarNavigateBackModule;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import rx.subjects.PublishSubject;

@Module(includes = {
        ToolbarNavigateBackModule.class,
        ActivityLauncherModule.class,
})
public abstract class AddMealActivityModule {

    private static final String ADD_MEAL_CONTENT = "add meal content";

    @Binds
    public abstract AddMealScreen addMealScreen(AddMealScreenImpl screen);

    @Provides
    public static AddMealFragment provideContent(FragmentManager fragmentManager,
                                                 AddMealActivityComponent component) {

        AddMealFragment fragment = (AddMealFragment) fragmentManager.findFragmentByTag(ADD_MEAL_CONTENT);
        if (fragment == null) {
            fragment = new AddMealFragment();

            fragmentManager.beginTransaction()
                    .add(R.id.add_meal_content_frame, fragment, ADD_MEAL_CONTENT)
                    .setTransitionStyle(FragmentTransaction.TRANSIT_NONE)
                    .commitNow();
        }
        fragment.setComponentFactory(component);
        return fragment;
    }

    @Provides
    @PerActivity
    public static PublishSubject<AddMealMenuAction> menuActionPublishSubject() {
        return PublishSubject.create();
    }

}
