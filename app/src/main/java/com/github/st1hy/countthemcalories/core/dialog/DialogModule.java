package com.github.st1hy.countthemcalories.core.dialog;

import com.github.st1hy.countthemcalories.core.dialog.DialogView;
import com.github.st1hy.countthemcalories.core.dialog.DialogViewController;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class DialogModule {

    @Binds
    public abstract DialogView dialogView(DialogViewController dialogViewController);
}
