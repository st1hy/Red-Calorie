package com.github.st1hy.countthemcalories.activities.tags.inject;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.tags.model.TagsActivityModel;
import com.github.st1hy.countthemcalories.activities.tags.model.TagsModel;
import com.github.st1hy.countthemcalories.activities.tags.presenter.TagsDaoAdapter;
import com.github.st1hy.countthemcalories.activities.tags.presenter.TagsPresenter;
import com.github.st1hy.countthemcalories.activities.tags.presenter.TagsPresenterImpl;
import com.github.st1hy.countthemcalories.activities.tags.view.TagsActivity;
import com.github.st1hy.countthemcalories.activities.tags.view.TagsView;
import com.github.st1hy.countthemcalories.core.drawer.presenter.DrawerPresenter;
import com.github.st1hy.countthemcalories.core.inject.PerActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class TagsModule {
    private final TagsActivity activity;

    public TagsModule(@NonNull TagsActivity activity) {
        this.activity = activity;
    }

    @Provides
    @PerActivity
    public Activity provideActivity() {
        return activity;
    }

    @Provides
    @PerActivity
    public TagsView provideView() {
        return activity;
    }


    @Provides
    @PerActivity
    public TagsPresenterImpl provideTagsDrawerPresenter(TagsView view, TagsDaoAdapter adapter) {
        return new TagsPresenterImpl(view, adapter);
    }

    @Provides
    @PerActivity
    public TagsPresenter providePresenter(TagsPresenterImpl presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    public TagsDaoAdapter providePresentedAdapter(TagsView view, TagsModel model, TagsActivityModel activityModel) {
        return new TagsDaoAdapter(view, model, activityModel);
    }

    @Provides
    @PerActivity
    public Intent provideIntent() {
        return activity.getIntent();
    }

    @Provides
    @PerActivity
    public DrawerPresenter provideDrawerPresenter(TagsPresenterImpl presenter) {
        return presenter;
    }
}
