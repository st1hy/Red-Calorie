package com.github.st1hy.countthemcalories.ui.activities.tags.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.ui.R;
import com.github.st1hy.countthemcalories.ui.activities.tags.presenter.TagsPresenter;
import com.github.st1hy.countthemcalories.ui.activities.tags.presenter.TagsStateSaver;
import com.github.st1hy.countthemcalories.ui.core.baseview.BaseFragment;
import com.github.st1hy.countthemcalories.ui.inject.core.FragmentModule;
import com.github.st1hy.countthemcalories.ui.inject.tags.TagsFragmentComponentFactory;

import javax.inject.Inject;

public class TagsFragment extends BaseFragment {

    public static final String ARG_PICK_BOOL = "pick tag flag";
    public static final String ARG_EXCLUDED_TAGS_STRING_ARRAY = "excluded tags array";

    private TagsFragmentComponentFactory componentFactory;

    @Inject
    TagsPresenter presenter;
    @Inject
    TagsStateSaver saver;
    @Inject
    RecyclerView recyclerView; //injects adapter

    public void setComponentFactory(TagsFragmentComponentFactory componentFactory) {
        this.componentFactory = componentFactory;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tags_content, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        componentFactory.newTagsFragmentComponent(new FragmentModule(this, savedInstanceState))
                .inject(this);
        componentFactory = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.onStop();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        saver.onSaveState(outState);
    }
}