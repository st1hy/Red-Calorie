package com.github.st1hy.countthemcalories.core.dialog;

import dagger.Module;
import dagger.Provides;

@Module
public class DialogModule {

    @Provides
    public DialogView dialogView(DialogViewController dialogViewController) {
        return dialogViewController;
    }
}
