package com.github.st1hy.countthemcalories.inject.activities.tags.fragment;

import com.github.st1hy.countthemcalories.activities.tags.fragment.TagsFragment;
import com.github.st1hy.countthemcalories.activities.tags.fragment.viewholder.inject.TagComponentFactory;
import com.github.st1hy.countthemcalories.inject.PerFragment;

import dagger.Subcomponent;

@PerFragment
@Subcomponent(modules = TagsFragmentModule.class)
public interface TagsFragmentComponent extends TagComponentFactory {

    void inject(TagsFragment fragment);
}
