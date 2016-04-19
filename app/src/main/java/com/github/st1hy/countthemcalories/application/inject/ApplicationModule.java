package com.github.st1hy.countthemcalories.application.inject;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.application.CaloriesCounterApplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {
    private final CaloriesCounterApplication application;

    public ApplicationModule(@NonNull CaloriesCounterApplication application) {
        this.application = application;
    }

    @Provides
    public Context provideContext() {
        return application.getBaseContext();
    }

    @Provides
    @Singleton
    public Application provideApplication() {
        return application;
    }


}
