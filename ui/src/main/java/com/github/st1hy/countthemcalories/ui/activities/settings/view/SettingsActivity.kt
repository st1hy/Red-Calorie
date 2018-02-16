package com.github.st1hy.countthemcalories.ui.activities.settings.view

import android.os.Bundle
import android.support.v4.app.Fragment
import com.github.st1hy.countthemcalories.ui.R
import com.github.st1hy.countthemcalories.ui.core.baseview.BaseActivity
import com.github.st1hy.countthemcalories.ui.core.drawer.DrawerPresenter
import com.github.st1hy.countthemcalories.ui.inject.core.ActivityModule
import javax.inject.Inject

internal class SettingsActivity : BaseActivity() {

    @Inject lateinit var drawerPresenter: DrawerPresenter
    @Inject lateinit var fragments: Map<String, Fragment> //injects component

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        getAppComponent().newSettingsActivityComponent(ActivityModule(this))
                .inject(this)
    }

    override fun onStart() {
        super.onStart()
        drawerPresenter!!.onStart()
    }

    override fun onStop() {
        super.onStop()
        drawerPresenter!!.onStop()
    }

    override fun onBackPressed() {
        if (drawerPresenter!!.onBackPressed()) super.onBackPressed()
    }
}