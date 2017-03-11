package com.github.st1hy.countthemcalories.activities.tags.fragment.inject;

import com.github.st1hy.countthemcalories.activities.tags.fragment.TagsFragment;
import com.github.st1hy.countthemcalories.activities.tags.fragment.adapter.inject.TagComponentFactory;
import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.github.st1hy.countthemcalories.inject.common.FragmentModule;

import dagger.Subcomponent;

@PerFragment
@Subcomponent(modules = {
        FragmentModule.class,
        TagsFragmentModule.class
})
public interface TagsFragmentComponent extends TagComponentFactory {

    void inject(TagsFragment fragment);
}
