package com.github.st1hy.countthemcalories.inject.module;

import android.app.Activity;
import android.support.v7.widget.Toolbar;

import com.github.st1hy.countthemcalories.AddMealActivity;
import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.inject.PerActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import dagger.Module;
import dagger.Provides;

@Module
public class AddMealActivityModule {
    private final AddMealActivity activity;

    @Bind(R.id.add_meal_toolbar)
    Toolbar toolbar;

    public AddMealActivityModule(AddMealActivity activity) {
        this.activity = activity;
        ButterKnife.bind(this, activity);
    }

    @Provides
    @PerActivity
    public Activity provideActivity() {
        return activity;
    }

    @Provides
    @PerActivity
    public Toolbar provideToolbar() {
        return toolbar;
    }
}
