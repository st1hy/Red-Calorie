package com.github.st1hy.countthemcalories.testutils;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.adapter.RecyclerEvent;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class RecyclerEventMatchers {

    @NonNull
    public static TypeSafeMatcher<RecyclerEvent> hasType(final RecyclerEvent.Type type) {
        return new TypeSafeMatcher<RecyclerEvent>() {
            @Override
            protected boolean matchesSafely(RecyclerEvent item) {
                return item.getType() == type;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("has type " + type);
            }
        };
    }

    @NonNull
    public static TypeSafeMatcher<RecyclerEvent> hasPosition(final int position) {
        return new TypeSafeMatcher<RecyclerEvent>() {
            @Override
            protected boolean matchesSafely(RecyclerEvent item) {
                return item.getPosition() == position;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("has position " + position);
            }
        };
    }
}
