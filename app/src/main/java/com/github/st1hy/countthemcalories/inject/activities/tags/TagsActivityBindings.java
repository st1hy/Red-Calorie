package com.github.st1hy.countthemcalories.inject.activities.tags;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.github.st1hy.countthemcalories.activities.tags.view.TagsScreen;
import com.github.st1hy.countthemcalories.activities.tags.view.TagsScreenImpl;
import com.github.st1hy.countthemcalories.inject.activities.tags.fragment.TagsFragmentComponentFactory;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class TagsActivityBindings {

    @Binds
    public abstract Activity activity(AppCompatActivity activity);

    @Binds
    @Named("activityContext")
    public abstract Context context(AppCompatActivity activity);

    @Binds
    public abstract TagsScreen tagsScreen(TagsScreenImpl screen);

    @Binds
    public abstract TagsFragmentComponentFactory tagsFragmentComponentFactory(TagsActivityComponent component);
}
