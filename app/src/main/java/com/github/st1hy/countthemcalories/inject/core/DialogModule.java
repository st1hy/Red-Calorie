package com.github.st1hy.countthemcalories.inject.core;

import com.github.st1hy.countthemcalories.core.dialog.DialogView;
import com.github.st1hy.countthemcalories.core.dialog.DialogViewController;

import dagger.Module;
import dagger.Provides;

@Module
public class DialogModule {

    @Provides
    public DialogView dialogView(DialogViewController dialogViewController) {
        return dialogViewController;
    }
}
