package com.github.st1hy.countthemcalories.matchers;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.view.View;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.core.Utils;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import static org.hamcrest.Matchers.nullValue;

public class ImageViewMatchers {

    public static Matcher<View> withNoDrawable() {
        return withDrawable(nullValue(Drawable.class));
    }

    public static Matcher<View> withDrawable(@DrawableRes final int resId) {
        return new BoundedMatcher<View, ImageView>(ImageView.class) {
            private Resources resources;
            private final Utils utils = new Utils();

            @Override
            public boolean matchesSafely(ImageView item) {
                try {
                    resources = item.getResources();
                    Context context = item.getContext();
                    Bitmap expectedBitmap = toBitmap(item.getDrawable());
                    Bitmap bitmap = toBitmap(utils.getDrawableSafely(context, resId));
                    return isEqual(expectedBitmap, bitmap);
                } finally {
                    resources = null;
                }
            }

            @Nullable
            private Bitmap toBitmap(@Nullable Drawable drawable) {
                if (drawable == null) return null;
                else if (drawable instanceof BitmapDrawable) {
                    return ((BitmapDrawable) drawable).getBitmap();
                } else {
                    throw new AssertionError("Cannot compare drawables");
                }
            }

            private boolean isEqual(Bitmap a, Bitmap b) {
                return a == null && b == null || a != null && b != null && (a == b || a.sameAs(b));
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with drawable: ");
                description.appendText(Matchers.resIdToString(resources, resId));
            }
        };
    }
    public static Matcher<View> withDrawable(final Matcher<? extends Drawable> matcher) {
        return new BoundedMatcher<View, ImageView>(ImageView.class) {

            @Override
            public boolean matchesSafely(ImageView item) {
                return matcher.matches(item.getDrawable());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with drawable matching ");
                description.appendDescriptionOf(matcher);
            }
        };
    }
}
