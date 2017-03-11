package com.github.st1hy.countthemcalories.activities.tags.fragment.adapter.inject;

import com.github.st1hy.countthemcalories.activities.tags.fragment.adapter.holder.TagItemHolder;
import com.github.st1hy.countthemcalories.activities.tags.fragment.adapter.holder.TagSpaceHolder;

import dagger.Subcomponent;

@PerTag
@Subcomponent(modules = TagModule.class)
public interface TagComponent {

    TagItemHolder tagItemHolder();

    TagSpaceHolder tagSpaceHolder();
}
