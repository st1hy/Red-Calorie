package com.github.st1hy.countthemcalories.activities.tags.fragment.inject;

import com.github.st1hy.countthemcalories.activities.tags.fragment.view.TagsFragment;
import com.github.st1hy.countthemcalories.core.inject.PerFragment;

import dagger.Subcomponent;

@PerFragment
@Subcomponent(modules = TagsFragmentModule.class)
public interface TagsFragmentComponent {

    void inject(TagsFragment fragment);
}
