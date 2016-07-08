package com.github.st1hy.countthemcalories.activities.tags.fragment.inject;

import android.support.v4.app.FragmentActivity;

import com.github.st1hy.countthemcalories.activities.tags.fragment.model.RxTagsDatabaseModel;
import com.github.st1hy.countthemcalories.activities.tags.fragment.model.TagsFragmentModel;
import com.github.st1hy.countthemcalories.activities.tags.fragment.model.TagsViewModel;
import com.github.st1hy.countthemcalories.activities.tags.fragment.model.commands.TagsDatabaseCommands;
import com.github.st1hy.countthemcalories.activities.tags.fragment.presenter.TagsDaoAdapter;
import com.github.st1hy.countthemcalories.activities.tags.fragment.view.TagsFragment;
import com.github.st1hy.countthemcalories.activities.tags.fragment.view.TagsView;
import com.github.st1hy.countthemcalories.activities.tags.view.TagsScreen;
import com.github.st1hy.countthemcalories.core.inject.PerFragment;
import com.google.common.base.Preconditions;

import dagger.Module;
import dagger.Provides;

@Module
public class TagsFragmentModule {
    private final TagsFragment fragment;

    public TagsFragmentModule(TagsFragment fragment) {
        this.fragment = fragment;
    }

    @Provides
    @PerFragment
    public TagsDaoAdapter providePresentedAdapter(TagsView view, RxTagsDatabaseModel model,
                                                  TagsFragmentModel fragmentModel,
                                                  TagsViewModel viewModel,
                                                  TagsDatabaseCommands commands) {
        return new TagsDaoAdapter(view, model, fragmentModel, viewModel, commands);
    }

    @Provides
    public TagsView provideView() {
        return fragment;
    }

    @Provides
    @PerFragment
    public TagsFragmentModel provideFragmentModel() {
        return new TagsFragmentModel(fragment.getArguments());
    }

    @Provides
    public TagsScreen provideTagsScreen() {
        FragmentActivity activity = fragment.getActivity();
        Preconditions.checkState(activity instanceof TagsScreen,
                "activity must implement " + TagsScreen.class.getSimpleName());
        return (TagsScreen) activity;
    }
}
