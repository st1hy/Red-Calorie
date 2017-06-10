package com.github.st1hy.countthemcalories.core.permissions;

import android.support.v4.app.Fragment;

import com.github.st1hy.countthemcalories.core.fragments.FragmentLocation;
import com.github.st1hy.countthemcalories.core.fragments.FragmentTransactionModule;

import java.util.Map;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.Reusable;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;

@Module(includes = FragmentTransactionModule.class)
public abstract class PermissionModule {

    @Provides
    @IntoMap
    @StringKey(PermissionFragment.TAG)
    @Reusable
    public static FragmentLocation intentHandlerClass() {
        return new FragmentLocation.Builder<>(PermissionFragment.class, PermissionFragment.TAG).build();
    }

    @Provides
    @Reusable
    public static PermissionFragment providePermissionFragment(Map<String, Fragment> fragments) {
        return (PermissionFragment) fragments.get(PermissionFragment.TAG);
    }

    @Binds
    public abstract PermissionSubject permissionSubject(PermissionFragment fragment);
}
