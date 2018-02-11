package com.github.st1hy.countthemcalories.ui.activities.tags.view

import android.os.Bundle
import android.support.v4.app.Fragment
import com.github.st1hy.countthemcalories.R
import com.github.st1hy.countthemcalories.ui.core.baseview.BaseActivity
import com.github.st1hy.countthemcalories.ui.core.drawer.DrawerPresenter
import com.github.st1hy.countthemcalories.ui.inject.core.ActivityModule
import javax.inject.Inject


class TagsActivity : BaseActivity() {

    @Inject lateinit var fragments: Map<String, Fragment> //injects  fragments
    @Inject lateinit var drawerPresenter: DrawerPresenter

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tags_activity)
        getAppComponent().newTagsActivityComponent(ActivityModule(this)).inject(this)
    }

    override fun onStart() {
        super.onStart()
        drawerPresenter.onStart()
    }

    override fun onStop() {
        super.onStop()
        drawerPresenter.onStop()
    }

    override fun onBackPressed() {
        if (drawerPresenter.onBackPressed()) super.onBackPressed()
    }

    companion object {
        const val actionPickTag = "pick tag"
        const val extraSelectedTags = "selected tags"
        const val extraTags = "extra tag"
    }
}