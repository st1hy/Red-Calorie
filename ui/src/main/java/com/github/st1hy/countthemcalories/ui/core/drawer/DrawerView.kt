package com.github.st1hy.countthemcalories.ui.core.drawer

import android.content.Intent
import android.support.annotation.IdRes
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.github.st1hy.countthemcalories.ui.core.state.Selection
import com.github.st1hy.countthemcalories.ui.inject.app.PerActivity
import kotlinx.android.synthetic.main.app_navigation_view.*
import kotlinx.android.synthetic.main.tags_activity.*
import kotlinx.android.synthetic.main.tags_app_bar.*
import javax.inject.Inject

@PerActivity class DrawerView @Inject constructor(private val activity: AppCompatActivity) {

    val toolbar: Toolbar = activity.toolbar
    val drawer = activity.drawer_layout

    init {
        activity.setSupportActionBar(toolbar)
    }

    fun isDrawerOpen(): Boolean = drawer.isDrawerOpen(GravityCompat.START)

    fun setNavigationItemSelectedListener(
            listener: NavigationView.OnNavigationItemSelectedListener?) {
        activity.nav_view.setNavigationItemSelectedListener(listener)
    }

    fun showNavigationAsUp() {
        toolbar.setNavigationOnClickListener { activity.onBackPressed() }
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun registerToggle(drawerToggle: ActionBarDrawerToggle) {
        drawer.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
    }

    fun unregisterDrawerToggle(drawerToggle: ActionBarDrawerToggle) {
        drawer.removeDrawerListener(drawerToggle)
    }

    fun closeDrawer() {
        drawer.closeDrawer(GravityCompat.START)
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