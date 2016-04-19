package com.github.st1hy.countthemcalories.application.inject;

import android.app.Application;
import android.content.Context;

import com.github.st1hy.countthemcalories.application.CaloriesCounterApplication;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    @Singleton
    Context getContext();

    @Singleton
    Application getApplication();

    void inject(CaloriesCounterApplication application);

}
