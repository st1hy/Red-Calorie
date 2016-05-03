package com.github.st1hy.countthemcalories.activities.tags.inject;

import com.github.st1hy.countthemcalories.core.inject.ApplicationTestComponent;
import com.github.st1hy.countthemcalories.core.inject.PerActivity;
import com.github.st1hy.countthemcalories.database.DaoSession;

import dagger.Component;

@PerActivity
@Component(modules = TagsModule.class, dependencies = ApplicationTestComponent.class)
public interface TagsTestComponent extends TagsComponent {

    DaoSession getDaoSession();
}
