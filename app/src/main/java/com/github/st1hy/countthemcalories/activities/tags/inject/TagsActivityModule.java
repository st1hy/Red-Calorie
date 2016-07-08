package com.github.st1hy.countthemcalories.activities.tags.inject;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.st1hy.countthemcalories.activities.tags.fragment.view.TagsFragment;
import com.github.st1hy.countthemcalories.activities.tags.presenter.TagsDrawerPresenter;
import com.github.st1hy.countthemcalories.activities.tags.view.TagsActivity;
import com.github.st1hy.countthemcalories.core.drawer.presenter.DrawerPresenter;
import com.github.st1hy.countthemcalories.core.drawer.view.DrawerView;
import com.github.st1hy.countthemcalories.core.inject.PerActivity;

import dagger.Module;
import dagger.Provides;

@Module
public class TagsActivityModule {
    private final TagsActivity activity;

    public TagsActivityModule(@NonNull TagsActivity activity) {
        this.activity = activity;
    }

    @Provides
    @PerActivity
    public DrawerPresenter provideDrawerPresenter(DrawerView drawerView) {
        return new TagsDrawerPresenter(drawerView);
    }

    @Provides
    public DrawerView provideDrawerView() {
        return activity;
    }

    @Provides
    @PerActivity
    public TagsFragment provideContent(Bundle arguments) {
        TagsFragment fragment = new TagsFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Provides
    public Bundle provideArguments(Boolean inSelectMode, String[] excludedTags) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(TagsFragment.ARG_PICK_BOOL, inSelectMode);
        bundle.putStringArray(TagsFragment.ARG_EXCLUDED_TAGS_STRING_ARRAY, excludedTags);
        return bundle;
    }

    @Provides
    public Boolean provideIsInSelectMode(@Nullable Intent intent) {
        return intent != null && TagsActivity.ACTION_PICK_TAG.equals(intent.getAction());
    }

    @Provides
    public String[] provideExcludedTags(@Nullable Intent intent) {
        String[] tags = null;
        if (intent != null) {
            tags = intent.getStringArrayExtra(TagsActivity.EXTRA_EXCLUDE_TAG_STRING_ARRAY);
        }
        if (tags == null) tags = new String[0];
        return tags;
    }

    @Provides
    @Nullable
    public Intent provideIntent() {
        return activity.getIntent();
    }
}
