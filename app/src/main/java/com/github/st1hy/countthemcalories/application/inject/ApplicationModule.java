package com.github.st1hy.countthemcalories.application.inject;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.application.CaloriesCounterApplication;
import com.github.st1hy.countthemcalories.application.vmpolicy.DaggerPolicyComponent;
import com.github.st1hy.countthemcalories.application.vmpolicy.PolicyComponent;

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
    public Application provideApplication() {
        return application;
    }

    @Provides
    public PolicyComponent providePolicyComponent() {
        return DaggerPolicyComponent.create();
    }

}
