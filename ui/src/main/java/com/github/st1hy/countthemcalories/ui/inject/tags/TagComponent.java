package com.github.st1hy.countthemcalories.ui.inject.tags;

import com.github.st1hy.countthemcalories.ui.activities.tags.adapter.holder.TagItemHolder;
import com.github.st1hy.countthemcalories.ui.activities.tags.adapter.holder.TagSpaceHolder;

import dagger.Subcomponent;

@PerTag
@Subcomponent(modules = TagModule.class)
public interface TagComponent {

    TagItemHolder tagItemHolder();

    TagSpaceHolder tagSpaceHolder();
}
