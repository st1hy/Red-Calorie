package com.github.st1hy.countthemcalories.ui.inject.app;

import android.app.Application;
import android.support.annotation.NonNull;

import dagger.Module;
import dagger.Provides;

@Module(includes = {ApplicationBindings.class, SettingsModule.class})
public class ApplicationModule {

    private final Application application;

    public ApplicationModule(@NonNull Application application) {
        this.application = application;
    }

    @Provides
    public Application provideApplication() {
        return application;
    }

}
