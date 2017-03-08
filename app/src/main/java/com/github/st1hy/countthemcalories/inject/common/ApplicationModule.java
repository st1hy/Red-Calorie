package com.github.st1hy.countthemcalories.inject.common;

import android.app.Application;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.application.CaloriesCounterApplication;

import dagger.Module;
import dagger.Provides;

@Module(includes = ApplicationBindings.class)
public class ApplicationModule {
    private final CaloriesCounterApplication application;

    public ApplicationModule(@NonNull CaloriesCounterApplication application) {
        this.application = application;
    }

    @Provides
    public Application provideContext() {
        return application;
    }


}
