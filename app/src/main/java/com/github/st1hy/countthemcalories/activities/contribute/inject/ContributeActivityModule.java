package com.github.st1hy.countthemcalories.activities.contribute.inject;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.github.st1hy.countthemcalories.activities.contribute.view.ContributeActivity;
import com.github.st1hy.countthemcalories.core.drawer.DrawerMenuItem;

import dagger.Module;
import dagger.Provides;

@Module
public class ContributeActivityModule {
    private final ContributeActivity activity;

    public ContributeActivityModule(@NonNull ContributeActivity activity) {
        this.activity = activity;
    }

    @Provides
    public AppCompatActivity appCompatActivity() {
        return activity;
    }

    @Provides
    public DrawerMenuItem currentItem() {
        return DrawerMenuItem.CONTRIBUTE;
    }

}
