package com.github.st1hy.countthemcalories.application.inject;

import android.app.Application;
import android.content.Context;

import com.github.st1hy.countthemcalories.application.CaloriesCounterApplication;
import com.github.st1hy.countthemcalories.database.DaoSession;
import com.github.st1hy.countthemcalories.database.application.inject.DatabaseModule;
import com.squareup.picasso.Picasso;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ApplicationModule.class, DatabaseModule.class})
public interface ApplicationComponent {

    @Singleton
    Context getContext();

    @Singleton
    Application getApplication();

    Picasso getPicasso();

    DaoSession getDaoSession();

    void inject(CaloriesCounterApplication application);

}
