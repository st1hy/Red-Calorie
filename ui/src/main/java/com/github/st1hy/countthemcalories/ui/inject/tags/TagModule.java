package com.github.st1hy.countthemcalories.ui.inject.tags;

import android.support.annotation.NonNull;
import android.view.View;

import dagger.Module;
import dagger.Provides;

@Module
public class TagModule {

    private final View tagRootView;

    public TagModule(@NonNull View tagRootView) {
        this.tagRootView = tagRootView;
    }

    @Provides
    @TagRootView
    public View tagRootView() {
        return tagRootView;
    }
}
