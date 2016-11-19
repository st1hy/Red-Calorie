package com.github.st1hy.countthemcalories.activities.tags.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.tags.fragment.presenter.TagsDaoAdapter;
import com.github.st1hy.countthemcalories.core.baseview.BaseFragment;
import com.github.st1hy.countthemcalories.inject.activities.tags.fragment.TagsFragmentComponentFactory;
import com.github.st1hy.countthemcalories.inject.activities.tags.fragment.TagsFragmentModule;

import javax.inject.Inject;

public class TagsFragment extends BaseFragment {

    public static final String ARG_PICK_BOOL = "pick tag flag";
    public static final String ARG_EXCLUDED_TAGS_STRING_ARRAY = "excluded tags array";

    private TagsFragmentComponentFactory componentFactory;

    @Inject
    TagsDaoAdapter adapter;
    @Inject
    RecyclerView recyclerView; //injects adapter

    public void setComponentFactory(TagsFragmentComponentFactory componentFactory) {
        this.componentFactory = componentFactory;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tags_content, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        componentFactory.newTagsFragmentComponent(new TagsFragmentModule(this))
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
}
