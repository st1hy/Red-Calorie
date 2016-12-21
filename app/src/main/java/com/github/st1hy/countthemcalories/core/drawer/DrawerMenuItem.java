package com.github.st1hy.countthemcalories.core.drawer;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.ingredients.IngredientsActivity;
import com.github.st1hy.countthemcalories.activities.overview.OverviewActivity;
import com.github.st1hy.countthemcalories.activities.settings.SettingsActivity;
import com.github.st1hy.countthemcalories.activities.tags.TagsActivity;
import com.github.st1hy.countthemcalories.core.baseview.BaseActivity;

public enum DrawerMenuItem {
    OVERVIEW(OverviewActivity.class, R.id.nav_overview),
    INGREDIENTS(IngredientsActivity.class, R.id.nav_ingredients),
    CATEGORIES(TagsActivity.class, R.id.nav_tags),
    SETTINGS(SettingsActivity.class, R.id.nav_settings);
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
