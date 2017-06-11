package com.github.st1hy.countthemcalories.core.dialog;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class DialogModule {

    @Binds
    public abstract DialogView dialogView(DialogViewController dialogViewController);
}
