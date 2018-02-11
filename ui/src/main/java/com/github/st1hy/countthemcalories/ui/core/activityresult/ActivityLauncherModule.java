package com.github.st1hy.countthemcalories.ui.core.activityresult;

import android.support.v4.app.Fragment;

import com.github.st1hy.countthemcalories.ui.core.FragmentLocation;
import com.github.st1hy.countthemcalories.ui.inject.core.FragmentTransactionModule;

import java.util.Map;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.Reusable;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;

@Module(includes = FragmentTransactionModule.class)
public abstract class ActivityLauncherModule {

    @Binds
    @Reusable
    public abstract ActivityLauncher activityLauncher(ActivityLauncherImp activityLauncherImp);

    @Binds
    public abstract ActivityLauncherSubject activityLauncherSubject(IntentHandlerFragment fragment);


    @Provides
    @IntoMap
    @StringKey(IntentHandlerFragment.TAG)
    public static FragmentLocation intentHandlerClass() {
        return new FragmentLocation.Builder<>(IntentHandlerFragment.class, IntentHandlerFragment.TAG).build();
    }

    @Provides
    @Reusable
    public static IntentHandlerFragment intentHandlerFragment(Map<String, Fragment> fragments) {
        return (IntentHandlerFragment) fragments.get(IntentHandlerFragment.TAG);
    }
}
