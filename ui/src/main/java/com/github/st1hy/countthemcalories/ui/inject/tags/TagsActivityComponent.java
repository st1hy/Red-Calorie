package com.github.st1hy.countthemcalories.ui.inject.tags;

import com.github.st1hy.countthemcalories.ui.activities.tags.view.TagsActivity;
import com.github.st1hy.countthemcalories.ui.inject.app.PerActivity;
import com.github.st1hy.countthemcalories.ui.inject.core.ActivityModule;

import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = {
        ActivityModule.class,
        TagsActivityModule.class,
})
public interface TagsActivityComponent extends TagsFragmentComponentFactory {

    void inject(TagsActivity activity);

}
