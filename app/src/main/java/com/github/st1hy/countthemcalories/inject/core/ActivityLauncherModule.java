package com.github.st1hy.countthemcalories.inject.core;

import android.support.v4.app.FragmentManager;

import com.github.st1hy.countthemcalories.core.activityresult.ActivityLauncher;
import com.github.st1hy.countthemcalories.core.activityresult.ActivityLauncherImp;
import com.github.st1hy.countthemcalories.core.activityresult.ActivityLauncherSubject;
import com.github.st1hy.countthemcalories.core.activityresult.IntentHandlerFragment;

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
    public static IntentHandlerFragment intentHandlerFragment(FragmentManager fragmentManager) {
        IntentHandlerFragment fragment = (IntentHandlerFragment) fragmentManager
                .findFragmentByTag(IntentHandlerFragment.TAG);
        if (fragment == null) {
            fragment = new IntentHandlerFragment();
            fragmentManager.beginTransaction()
                    .add(fragment, IntentHandlerFragment.TAG)
                    .commitNow();
        }
        return fragment;
    }
}
