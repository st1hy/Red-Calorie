package com.github.st1hy.countthemcalories.activities.tags.inject;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.tags.view.TagsActivity;
import com.github.st1hy.countthemcalories.activities.tags.view.TagsScreen;
import com.github.st1hy.countthemcalories.inject.application.ApplicationComponent;
import com.github.st1hy.countthemcalories.inject.core.DrawerModule;
import com.github.st1hy.countthemcalories.inject.PerActivity;

import dagger.Component;

@PerActivity
@Component(modules = {TagsActivityModule.class, DrawerModule.class},
        dependencies = ApplicationComponent.class)
public interface TagsActivityComponent {

    void inject(@NonNull TagsActivity activity);

    TagsScreen tagsScreen();
}
