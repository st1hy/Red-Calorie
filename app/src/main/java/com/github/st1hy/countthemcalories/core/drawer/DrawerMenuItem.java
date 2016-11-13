package com.github.st1hy.countthemcalories.core.drawer;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.contribute.view.ContributeActivity;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsActivity;
import com.github.st1hy.countthemcalories.activities.overview.view.OverviewActivity;
import com.github.st1hy.countthemcalories.activities.settings.view.SettingsActivity;
import com.github.st1hy.countthemcalories.activities.tags.view.TagsActivity;
import com.github.st1hy.countthemcalories.core.baseview.BaseActivity;

public enum DrawerMenuItem {
    OVERVIEW(OverviewActivity.class, R.id.nav_overview),
    INGREDIENTS(IngredientsActivity.class, R.id.nav_ingredients),
    CATEGORIES(TagsActivity.class, R.id.nav_tags),
    SETTINGS(SettingsActivity.class, R.id.nav_settings),
    CONTRIBUTE(ContributeActivity.class, R.id.nav_contribute);
    private final Class<? extends BaseActivity> activityClass;
    @IdRes
    private final int menuItemId;

    DrawerMenuItem(@NonNull Class<? extends BaseActivity> activityClass,
                   @IdRes int menuItemId) {
        this.activityClass = activityClass;
        this.menuItemId = menuItemId;
    }

    @NonNull
    public Class<? extends BaseActivity> getActivityClass() {
        return activityClass;
    }

    @IdRes
    public int getMenuItemId() {
        return menuItemId;
    }

    @Nullable
    public static DrawerMenuItem findByMenuId(@IdRes int menuItemId) {
        for (DrawerMenuItem item : values()) {
            if (item.getMenuItemId() == menuItemId)
                return item;
        }
        return null;
    }
}
