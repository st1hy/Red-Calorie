package com.github.st1hy.countthemcalories.ui.core.command.undo.inject;

import com.github.st1hy.countthemcalories.ui.core.command.undo.UndoView;
import com.github.st1hy.countthemcalories.ui.core.command.undo.UndoViewImpl;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class UndoModule {

    @Binds
    public abstract UndoView undoView(UndoViewImpl undoView);
}
