package com.github.st1hy.countthemcalories.inject.activities.tags;

import com.github.st1hy.countthemcalories.activities.tags.TagsActivity;
import com.github.st1hy.countthemcalories.inject.PerActivity;
import com.github.st1hy.countthemcalories.inject.activities.tags.fragment.TagsFragmentComponentFactory;
import com.github.st1hy.countthemcalories.inject.core.DrawerModule;
import com.github.st1hy.countthemcalories.inject.core.UndoModule;

import dagger.Subcomponent;

@PerActivity
@Subcomponent(modules = {
        TagsActivityModule.class,
        DrawerModule.class,
        UndoModule.class
})
public interface TagsActivityComponent extends TagsFragmentComponentFactory {

    void inject(TagsActivity activity);

}
