package com.github.st1hy.countthemcalories.activities.tags.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.tags.fragment.adapter.TagsDaoAdapter;
import com.github.st1hy.countthemcalories.activities.tags.fragment.inject.TagsFragmentComponentFactory;
import com.github.st1hy.countthemcalories.activities.tags.fragment.presenter.TagsStateSaver;
import com.github.st1hy.countthemcalories.core.baseview.BaseFragment;
import com.github.st1hy.countthemcalories.inject.common.FragmentModule;

import javax.inject.Inject;

public class TagsFragment extends BaseFragment {

    public static final String ARG_PICK_BOOL = "pick tag flag";
    public static final String ARG_EXCLUDED_TAGS_STRING_ARRAY = "excluded tags array";

    private TagsFragmentComponentFactory componentFactory;

    @Inject
    TagsDaoAdapter adapter;
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
        adapter.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.onStop();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        saver.onSaveState(outState);
    }
}
