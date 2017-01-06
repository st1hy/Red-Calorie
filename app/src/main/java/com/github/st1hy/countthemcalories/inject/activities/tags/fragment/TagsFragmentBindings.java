package com.github.st1hy.countthemcalories.inject.activities.tags.fragment;

import com.github.st1hy.countthemcalories.activities.tags.fragment.view.TagsView;
import com.github.st1hy.countthemcalories.activities.tags.fragment.view.TagsViewImpl;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class TagsFragmentBindings {

    @Binds
    public abstract TagsView provideView(TagsViewImpl view);
}
