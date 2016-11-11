package com.github.st1hy.countthemcalories.core.headerpicture.view;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class WithPictureActivityTestUtils {

    public static void setTempUri(WithPictureActivity activity, Uri uri) {
        activity.tempImageUri = uri;
    }

    @NonNull
    public static TypeSafeMatcher<Intent> injectUriOnMatch(final WithPictureActivity activity,
                                                           final Uri uri) {
        return new TypeSafeMatcher<Intent>() {
            @Override
            protected boolean matchesSafely(Intent item) {
                WithPictureActivityTestUtils.setTempUri(activity, uri);
                return true;
            }

            @Override
            public void describeTo(Description description) {

            }
        };
    }
}