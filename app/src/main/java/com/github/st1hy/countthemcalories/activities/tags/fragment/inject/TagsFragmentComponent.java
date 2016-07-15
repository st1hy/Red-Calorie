package com.github.st1hy.countthemcalories.activities.tags.fragment.inject;

import com.github.st1hy.countthemcalories.activities.tags.fragment.view.TagsFragment;
import com.github.st1hy.countthemcalories.application.inject.ApplicationComponent;
import com.github.st1hy.countthemcalories.core.inject.PerFragment;

import dagger.Component;

@PerFragment
@Component(modules = TagsFragmentModule.class, dependencies = ApplicationComponent.class)
public interface TagsFragmentComponent {

    void inject(TagsFragment fragment);
}
