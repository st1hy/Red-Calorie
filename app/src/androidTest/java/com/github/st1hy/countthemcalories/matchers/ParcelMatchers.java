package com.github.st1hy.countthemcalories.matchers;

import android.os.Parcelable;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.parceler.Parcels;

public class ParcelMatchers {

    public static <T> Matcher<Parcelable> unparceled(Matcher<T> matcher) {
        return new TypeSafeMatcher<Parcelable>() {
            @Override
            protected boolean matchesSafely(Parcelable item) {
                return matcher.matches(Parcels.unwrap(item));
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("has Parcelable with content matching: ")
                        .appendDescriptionOf(matcher);
            }
        };
    }
}
