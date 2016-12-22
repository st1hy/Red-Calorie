package com.github.st1hy.countthemcalories.inject.core;

import android.support.v4.app.FragmentManager;

import com.github.st1hy.countthemcalories.core.permissions.PermissionFragment;
import com.github.st1hy.countthemcalories.core.permissions.PermissionSubject;

import dagger.Module;
import dagger.Provides;

@Module
public class PermissionModule {

    @Provides
    public PermissionSubject providePermissionSubject(FragmentManager fragmentManager) {
        PermissionFragment fragment = (PermissionFragment) fragmentManager
                .findFragmentByTag(PermissionFragment.TAG);
        if (fragment == null) {
            fragment = new PermissionFragment();
            fragmentManager.beginTransaction()
                    .add(fragment, PermissionFragment.TAG)
                    .commitNow();
        }
        return fragment;
    }
}
