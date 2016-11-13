package com.github.st1hy.countthemcalories.activities.tags.fragment.inject;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.tags.fragment.model.TagsFragmentModel;
import com.github.st1hy.countthemcalories.activities.tags.fragment.presenter.TagsDaoAdapter;
import com.github.st1hy.countthemcalories.activities.tags.fragment.view.TagsFragment;
import com.github.st1hy.countthemcalories.activities.tags.fragment.view.TagsView;
import com.github.st1hy.countthemcalories.activities.tags.fragment.view.TagsViewImpl;
import com.github.st1hy.countthemcalories.core.inject.PerFragment;

import dagger.Module;
import dagger.Provides;

@Module
public class TagsFragmentModule {
    private final TagsFragment fragment;

    public TagsFragmentModule(TagsFragment fragment) {
        this.fragment = fragment;
    }

    @Provides
    public TagsView provideView(TagsViewImpl view) {
        return view;
    }

    @Provides
    @PerFragment
    public TagsFragmentModel provideFragmentModel() {
        return new TagsFragmentModel(fragment.getArguments());
    }

    @Provides
    @PerFragment
    public RecyclerView recyclerView(View rootView, Context context, TagsDaoAdapter adapter) {
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.tags_recycler);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        return recyclerView;
    }


}
