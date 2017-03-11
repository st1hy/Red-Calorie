package com.github.st1hy.countthemcalories.activities.tags.fragment.adapter.inject;

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
