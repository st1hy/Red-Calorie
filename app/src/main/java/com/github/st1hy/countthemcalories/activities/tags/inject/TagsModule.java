package com.github.st1hy.countthemcalories.activities.tags.inject;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.github.st1hy.countthemcalories.activities.tags.model.TagsActivityModel;
import com.github.st1hy.countthemcalories.activities.tags.model.TagsModel;
import com.github.st1hy.countthemcalories.activities.tags.presenter.TagsPresenter;
import com.github.st1hy.countthemcalories.activities.tags.presenter.TagsPresenterImp;
import com.github.st1hy.countthemcalories.activities.tags.view.TagsActivity;
import com.github.st1hy.countthemcalories.activities.tags.view.TagsView;
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
    public TagsPresenter providePresenter(TagsPresenterImp presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    public TagsPresenterImp providePresentedAdapter(TagsView view, TagsModel model, TagsActivityModel activityModel) {
        return new TagsPresenterImp(view, model, activityModel);
    }
    @Provides
    @PerActivity
    public RecyclerView.Adapter provideAdapter(TagsPresenterImp presenter) {
        return presenter;
    }

    @Provides
    @PerActivity
    public Intent provideIntent() {
        return activity.getIntent();
    }
}
