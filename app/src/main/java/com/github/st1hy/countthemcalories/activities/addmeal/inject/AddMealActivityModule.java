package com.github.st1hy.countthemcalories.activities.addmeal.inject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.AddMealFragment;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.inject.AddMealFragmentModule;
import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealMenuAction;
import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealScreen;
import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealScreenImpl;
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
public abstract class AddMealActivityModule {

    private static final String ADD_MEAL_CONTENT = "add meal content";

    @Binds
    public abstract AddMealScreen addMealScreen(AddMealScreenImpl screen);

    @Provides
    public static AddMealFragment provideContent(FragmentManager fragmentManager,
                                                 Bundle arguments,
                                                 AddMealActivityComponent component) {

        AddMealFragment fragment = (AddMealFragment) fragmentManager.findFragmentByTag(ADD_MEAL_CONTENT);
        if (fragment == null) {
            fragment = new AddMealFragment();
            fragment.setArguments(arguments);

            fragmentManager.beginTransaction()
                    .add(R.id.add_meal_content_frame, fragment, ADD_MEAL_CONTENT)
                    .setTransitionStyle(FragmentTransaction.TRANSIT_NONE)
                    .commitNow();
        }
        fragment.setComponentFactory(component);
        return fragment;
    }

    @Provides
    public static Bundle provideArguments(Intent intent) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(AddMealFragmentModule.EXTRA_MEAL_PARCEL,
                intent.getParcelableExtra(AddMealFragmentModule.EXTRA_MEAL_PARCEL));
        arguments.putParcelable(AddMealFragmentModule.EXTRA_INGREDIENT_PARCEL,
                intent.getParcelableExtra(AddMealFragmentModule.EXTRA_INGREDIENT_PARCEL));
        arguments.putSerializable(AddMealFragmentModule.EXTRA_NEW_MEAL_DATE,
                intent.getSerializableExtra(AddMealFragmentModule.EXTRA_NEW_MEAL_DATE));
        return arguments;
    }

    @Provides
    @PerActivity
    public static PublishSubject<AddMealMenuAction> menuActionPublishSubject() {
        return PublishSubject.create();
    }

}
