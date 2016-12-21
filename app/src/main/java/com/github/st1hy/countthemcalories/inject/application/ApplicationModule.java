package com.github.st1hy.countthemcalories.inject.application;

import android.content.Context;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.application.CaloriesCounterApplication;
import com.github.st1hy.countthemcalories.core.activityresult.RxActivityResult;
import com.github.st1hy.countthemcalories.core.permissions.PersistentPermissionCache;
import com.squareup.picasso.MediaStoreRequestHandlerNext;
import com.squareup.picasso.Picasso;

import javax.inject.Named;
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
    @Named("appContext")
    public Context provideContext() {
        return application.getBaseContext();
    }


    @Provides
    @Singleton
    public Picasso providePicasso(@Named("appContext") Context context) {
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
    public RxActivityResult rxActivityResult(@Named("appContext") Context context) {
        return new RxActivityResult(context.getPackageName());
    }

}
