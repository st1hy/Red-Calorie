package com.github.st1hy.countthemcalories.testutils;

import android.support.annotation.NonNull;

import com.google.common.base.Optional;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class OptionalMatchers {

    @NonNull
    public static <T> Matcher<Optional<T>> isAbsent() {
        return new TypeSafeMatcher<Optional<T>>() {
            @Override
            protected boolean matchesSafely(Optional<T> item) {
                return !item.isPresent();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is absent");
            }
        };
    }

    public static <T> Matcher<Optional<T>> equalTo(final T tData) {
        return new TypeSafeMatcher<Optional<T>>() {

            @Override
            protected boolean matchesSafely(Optional<T> item) {
                return tData.equals(item.orNull());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is equal to: " + tData);
            }
        };
    }

    public static <T> Matcher<Optional<T>> valueMatches(@NonNull final Matcher<T> matcher) {
        return new TypeSafeMatcher<Optional<T>>() {

            @Override
            protected boolean matchesSafely(Optional<T> item) {
                return matcher.matches(item.orNull());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with value ");
                description.appendDescriptionOf(matcher);
            }
        };
    }
}
