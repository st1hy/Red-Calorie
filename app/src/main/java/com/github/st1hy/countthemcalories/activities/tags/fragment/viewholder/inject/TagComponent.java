package com.github.st1hy.countthemcalories.activities.tags.fragment.viewholder.inject;

import com.github.st1hy.countthemcalories.activities.tags.fragment.viewholder.TagItemHolder;
import com.github.st1hy.countthemcalories.activities.tags.fragment.viewholder.TagSpaceHolder;

import dagger.Subcomponent;

@PerTag
@Subcomponent(modules = TagModule.class)
public interface TagComponent {

    TagItemHolder tagItemHolder();

    TagSpaceHolder tagSpaceHolder();
}
