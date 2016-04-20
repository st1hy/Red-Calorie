package com.github.st1hy.countthemcalories.application.vmpolicy;

import android.os.StrictMode;

import dagger.Module;
import dagger.Provides;

@Module
public class PolicyModule {

    @Provides
    public StrictMode.VmPolicy provideVmPolicy() {
        return new StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .penaltyDeath()
                .build();
    }

    @Provides
    public StrictMode.ThreadPolicy provideThreadPolicy() {
        return new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build();
    }
}
