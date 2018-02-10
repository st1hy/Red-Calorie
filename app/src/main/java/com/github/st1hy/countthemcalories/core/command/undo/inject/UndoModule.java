package com.github.st1hy.countthemcalories.core.command.undo.inject;

import com.github.st1hy.countthemcalories.database.commands.UndoView;
import com.github.st1hy.countthemcalories.core.command.undo.UndoViewImpl;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class UndoModule {

    @Binds
    public abstract UndoView undoView(UndoViewImpl undoView);
}
