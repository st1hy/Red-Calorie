package com.github.st1hy.countthemcalories.inject.application;

import android.content.Context;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.application.CaloriesCounterApplication;
import com.github.st1hy.countthemcalories.database.inject.DatabaseModule;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module(includes = {
        SettingsModule.class,
        DatabaseModule.class
})
public class ApplicationModule {
    private final CaloriesCounterApplication application;

    public ApplicationModule(@NonNull CaloriesCounterApplication application) {
        this.application = application;
    }

    @Provides
    @Named("appContext")
    public Context provideContext() {
        return application.getApplicationContext();
    }


}
