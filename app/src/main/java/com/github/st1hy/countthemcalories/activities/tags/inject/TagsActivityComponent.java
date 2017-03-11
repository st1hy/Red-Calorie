package com.github.st1hy.countthemcalories.activities.tags.inject;

import com.github.st1hy.countthemcalories.activities.tags.TagsActivity;
import com.github.st1hy.countthemcalories.activities.tags.fragment.inject.TagsFragmentComponentFactory;
import com.github.st1hy.countthemcalories.inject.PerActivity;
import com.github.st1hy.countthemcalories.inject.common.ActivityModule;

import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = {
        ActivityModule.class,
        TagsActivityModule.class,
})
public interface TagsActivityComponent extends TagsFragmentComponentFactory {

    void inject(TagsActivity activity);

}
