package com.github.st1hy.countthemcalories.application.inject;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.application.CaloriesCounterApplication;
import com.github.st1hy.countthemcalories.core.permissions.PersistentPermissionCache;
import com.github.st1hy.countthemcalories.core.rx.activityresult.RxActivityResult;
import com.squareup.picasso.MediaStoreRequestHandlerNext;
import com.squareup.picasso.Picasso;

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
    public Application provideApplication() {
        return application;
    }

    @Provides
    @Singleton
    public Picasso providePicasso(Context context) {
        return new Picasso.Builder(context)
                .addRequestHandler(new MediaStoreRequestHandlerNext(context))
                .build();
    }

    @Provides
    @Singleton
    public PersistentPermissionCache providePermissionCache() {
        return new PersistentPermissionCache();
    }

    @Provides
    @Singleton
    public RxActivityResult rxActivityResult(Context context) {
        return new RxActivityResult(context.getPackageName());
    }

}
