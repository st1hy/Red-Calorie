package com.github.st1hy.countthemcalories.inject.activities.overview;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.github.st1hy.countthemcalories.activities.overview.view.OverviewScreen;
import com.github.st1hy.countthemcalories.activities.overview.view.OverviewScreenImpl;
import com.github.st1hy.countthemcalories.inject.activities.overview.fragment.OverviewFragmentComponentFactory;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class OverviewActivityBindings {

    @Binds
    @Named("activityContext")
    public abstract Context activityContext(AppCompatActivity activity);

    @Binds
    public abstract Activity activity(AppCompatActivity activity);

    @Binds
    public abstract OverviewScreen overviewScreen(OverviewScreenImpl screen);

    @Binds
    public abstract OverviewFragmentComponentFactory fragmentComponentFactory(OverviewActivityComponent component);
}