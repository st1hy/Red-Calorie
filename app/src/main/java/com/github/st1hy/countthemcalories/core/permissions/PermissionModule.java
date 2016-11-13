package com.github.st1hy.countthemcalories.core.permissions;

import android.support.v4.app.FragmentManager;

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
                    .commit();
            fragmentManager.executePendingTransactions();
        }
        return fragment;
    }
}
