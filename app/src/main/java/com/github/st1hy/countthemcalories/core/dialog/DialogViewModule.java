package com.github.st1hy.countthemcalories.core.dialog;

import android.app.Activity;

import com.github.st1hy.countthemcalories.core.inject.PerActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class DialogViewModule {

    @PerActivity
    @Provides
    public DialogView dialogView(Activity activity) {
        return new DialogViewController(activity);
    }
}
