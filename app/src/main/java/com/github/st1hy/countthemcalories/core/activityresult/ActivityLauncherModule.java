package com.github.st1hy.countthemcalories.core.activityresult;

import android.support.v4.app.FragmentManager;

import com.github.st1hy.countthemcalories.inject.PerActivity;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.Reusable;

@Module
public abstract class ActivityLauncherModule {

    @Binds
    @Reusable
    public abstract ActivityLauncher activityLauncher(ActivityLauncherImp activityLauncherImp);

    @Binds
    public abstract ActivityLauncherSubject activityLauncherSubject(IntentHandlerFragment fragment);

    @Provides
    @PerActivity
    public static IntentHandlerFragment intentHandlerFragment(FragmentManager fragmentManager) {
        IntentHandlerFragment fragment = (IntentHandlerFragment) fragmentManager
                .findFragmentByTag(IntentHandlerFragment.TAG);
        if (fragment == null) {
            fragment = new IntentHandlerFragment();
            fragment.setRetainInstance(true);
            fragmentManager.beginTransaction()
                    .add(fragment, IntentHandlerFragment.TAG)
                    .commit();
        }
        return fragment;
    }
}
