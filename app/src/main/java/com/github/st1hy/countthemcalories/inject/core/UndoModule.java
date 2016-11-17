package com.github.st1hy.countthemcalories.inject.core;

import com.github.st1hy.countthemcalories.core.command.undo.UndoView;
import com.github.st1hy.countthemcalories.core.command.undo.UndoViewImpl;

import dagger.Module;
import dagger.Provides;

@Module
public class UndoModule {

    @Provides
    public UndoView undoView(UndoViewImpl undoView) {
        return undoView;
    }
}
