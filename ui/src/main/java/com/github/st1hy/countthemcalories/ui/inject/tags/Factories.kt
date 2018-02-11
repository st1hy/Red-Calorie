package com.github.st1hy.countthemcalories.ui.inject.tags

import com.github.st1hy.countthemcalories.ui.inject.core.ActivityModule
import com.github.st1hy.countthemcalories.ui.inject.core.FragmentModule

interface TagsFragmentComponentFactory {

    fun newTagsFragmentComponent(module: FragmentModule): TagsFragmentComponent
}

interface TagsActivityComponentFactory {

    fun newTagsActivityComponent(module: ActivityModule): TagsActivityComponent

//    fun inject(activity: TagsActivity) =
//            newTagsActivityComponent(ActivityModule(activity)).inject(activity)
}

interface TagComponentFactory {

    fun newTagComponent(tagModule: TagModule): TagComponent
}