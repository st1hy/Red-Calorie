package com.github.st1hy.countthemcalories.core;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public enum Utils {
    ;

    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean hasMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }


    /**
     * @return drawable or null if drawable could not be found
     */
    @Nullable
    public static Drawable getDrawableSafely(@NonNull Context context, @DrawableRes final int resId) {
        try {
            return getDrawable(context, resId);
        } catch (Resources.NotFoundException e) {
            return null;
        }
    }

    /**
     * @return drawable from resources
     * @throws Resources.NotFoundException if resource was not found
     */
    @NonNull
    public static Drawable getDrawable(@NonNull Context context,
                                       @DrawableRes int drawableResId) {
        Resources resources = context.getResources();
        int density = resources.getDisplayMetrics().densityDpi;
        if (hasLollipop()) {
            return getDrawableL(resources, context.getTheme(), drawableResId, density);
        } else {
            return getDrawableDeprecated(resources, drawableResId, density);
        }
    }

    @TargetApi(21)
    private static Drawable getDrawableL(@NonNull Resources resources,
                                         @NonNull Resources.Theme theme,
                                         @DrawableRes int drawableResId,
                                         int density) {
        return resources.getDrawableForDensity(drawableResId, density, theme);
    }

    @SuppressWarnings("deprecation")
    private static Drawable getDrawableDeprecated(@NonNull Resources resources,
                                                  @DrawableRes int drawableResId,
                                                  int density) {
        return resources.getDrawableForDensity(drawableResId, density);
    }
}