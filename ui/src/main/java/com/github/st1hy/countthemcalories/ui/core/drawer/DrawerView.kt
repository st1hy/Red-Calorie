package com.github.st1hy.countthemcalories.ui.core.drawer

import android.content.Intent
import android.support.annotation.IdRes
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import com.github.st1hy.countthemcalories.ui.core.state.Selection
import com.github.st1hy.countthemcalories.ui.inject.app.PerActivity
import kotlinx.android.synthetic.main.app_navigation_view.*
import kotlinx.android.synthetic.main.tags_activity.*
import kotlinx.android.synthetic.main.tags_app_bar.*
import javax.inject.Inject

@PerActivity class DrawerView @Inject constructor(private val activity: AppCompatActivity) {

    fun isDrawerOpen(): Boolean = activity.drawer_layout.isDrawerOpen(GravityCompat.START)

    init {
        activity.setSupportActionBar(activity.toolbar)
    }

    fun setNavigationItemSelectedListener(
            listener: NavigationView.OnNavigationItemSelectedListener?) {
        activity.nav_view.setNavigationItemSelectedListener(listener)
    }

    fun showNavigationAsUp() {
        activity.toolbar.setNavigationOnClickListener { activity.onBackPressed() }
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun registerToggle(drawerToggle: ActionBarDrawerToggle) {
        activity.drawer_layout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
    }

    fun unregisterDrawerToggle(drawerToggle: ActionBarDrawerToggle) {
        activity.drawer_layout.removeDrawerListener(drawerToggle)
    }

    fun closeDrawer() {
        activity.drawer_layout.closeDrawer(GravityCompat.START)
    }

    fun openDrawerActivity(item: DrawerMenuItem) {
        val intent = Intent(activity, item.activityClass)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        activity.startActivity(intent)
    }

    fun setMenuItemSelection(@IdRes menuId: Int, selected: Selection) {
        activity.nav_view.menu.findItem(menuId).isChecked = selected.isSelected
    }
}