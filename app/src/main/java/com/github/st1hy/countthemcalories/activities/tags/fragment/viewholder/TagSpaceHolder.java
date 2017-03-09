package com.github.st1hy.countthemcalories.activities.tags.fragment.viewholder;

import android.support.annotation.NonNull;
import android.view.View;

import com.github.st1hy.countthemcalories.activities.tags.fragment.viewholder.inject.TagRootView;

import javax.inject.Inject;

public class TagSpaceHolder extends TagViewHolder {

    @Inject
    public TagSpaceHolder(@NonNull @TagRootView  View itemView) {
        super(itemView);
    }

    @Override
    public void onAttached() {
    }

    @Override
    public void onDetached() {

    }
}
