package com.github.st1hy.countthemcalories.ui.inject.core;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.github.st1hy.countthemcalories.ui.core.FragmentLocation;
import com.github.st1hy.countthemcalories.ui.inject.app.PerActivity;

import java.util.HashMap;
import java.util.Map;

import dagger.Module;
import dagger.Provides;

@Module
public abstract class FragmentTransactionModule {

    @Provides
    @PerActivity
    static Map<String, Fragment> loadFragments(FragmentManager fragmentManager,
                                               Map<String, FragmentLocation> providedFragments) {
        Map<String, Fragment> fragments = new HashMap<>(providedFragments.size());
        FragmentTransaction fragmentTransaction = null;
        for (FragmentLocation fragmentLocation : providedFragments.values()) {
            String tag = fragmentLocation.getTag();
            Fragment fragment = fragmentLocation.findFragmentIn(fragmentManager);
            if (fragment == null) {
                fragment = fragmentLocation.newFragment();
                if (fragmentTransaction == null) {
                    fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setReorderingAllowed(true);
                }
                int rootViewId = fragmentLocation.getRootViewId();
                if (rootViewId != FragmentLocation.NO_ID) {
                    fragmentTransaction.add(rootViewId, fragment, tag);
                } else {
                    fragmentTransaction.add(fragment, tag);
                }
            }
            fragments.put(tag, fragment);
        }
        if (fragmentTransaction != null) fragmentTransaction.commitNow();
        return fragments;
    }

}
