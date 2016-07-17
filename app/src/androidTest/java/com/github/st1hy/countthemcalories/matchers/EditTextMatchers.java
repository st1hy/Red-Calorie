package com.github.st1hy.countthemcalories.matchers;

import android.support.test.espresso.matcher.BoundedMatcher;
import android.view.View;
import android.widget.EditText;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class EditTextMatchers {

    public static Matcher<? super View> hasNoError() {
        return new BoundedMatcher<View, EditText>(EditText.class) {
            @Override
            protected boolean matchesSafely(EditText item) {
                return item.getError() == null;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("without error ");
            }
        };
    }

}
