package com.github.st1hy.countthemcalories.application.inject;

import android.app.Application;
import android.content.Context;

import com.github.st1hy.countthemcalories.inject.quantifier.context.AppContext;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class ApplicationBindings {

    @AppContext
    @Binds
    public abstract Context context(Application application);

    @Named("appContext")
    @Binds
    public abstract Context context2(Application application);

}
