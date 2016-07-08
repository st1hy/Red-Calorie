package com.github.st1hy.countthemcalories.activities.tags.inject;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.tags.fragment.view.TagsFragment;
import com.github.st1hy.countthemcalories.activities.tags.view.TagsActivity;
import com.github.st1hy.countthemcalories.application.inject.ApplicationComponent;
import com.github.st1hy.countthemcalories.core.inject.PerActivity;

import dagger.Component;

@PerActivity
@Component(modules = TagsActivityModule.class, dependencies = ApplicationComponent.class)
public interface TagsActivityComponent {

    void inject(@NonNull TagsActivity activity);

    TagsFragment getContent();
}
