package com.github.st1hy.countthemcalories.ui.inject.tags;

import com.github.st1hy.countthemcalories.ui.activities.tags.view.TagsFragment;
import com.github.st1hy.countthemcalories.ui.inject.app.PerFragment;
import com.github.st1hy.countthemcalories.ui.inject.core.FragmentModule;

import dagger.Subcomponent;

@PerFragment
@Subcomponent(modules = {
        FragmentModule.class,
        TagsFragmentModule.class
})
public interface TagsFragmentComponent extends TagComponentFactory {

    void inject(TagsFragment fragment);
}
