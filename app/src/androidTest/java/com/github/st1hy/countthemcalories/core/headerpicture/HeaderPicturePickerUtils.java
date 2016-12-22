package com.github.st1hy.countthemcalories.core.headerpicture;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.application.CaloriesCounterApplication;
import com.github.st1hy.countthemcalories.core.baseview.BaseActivity;
import com.github.st1hy.countthemcalories.inject.ApplicationTestComponent;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class HeaderPicturePickerUtils {

    public static void setTempUri(BaseActivity activity, Uri uri) {
        CaloriesCounterApplication application = (CaloriesCounterApplication) activity
                .getApplication();
        ApplicationTestComponent component = (ApplicationTestComponent) application.getComponent();
//        component.newAddIngredientActivityComponent()
//        picturePicker.setTempImageUri(uri);
    }

    @NonNull
    public static TypeSafeMatcher<Intent> injectUriOnMatch(final BaseActivity activity,
                                                           final Uri uri) {
        return new TypeSafeMatcher<Intent>() {
            @Override
            protected boolean matchesSafely(Intent item) {
                HeaderPicturePickerUtils.setTempUri(activity, uri);
                return true;
            }

            @Override
            public void describeTo(Description description) {

            }
        };
    }
}