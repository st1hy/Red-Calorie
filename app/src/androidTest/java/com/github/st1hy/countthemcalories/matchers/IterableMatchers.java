package com.github.st1hy.countthemcalories.matchers;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class IterableMatchers {
    private IterableMatchers() {
    }

    /**
     * Matches all items of provided matcher to source. If any fails this fails.
     */
    public static <T> Matcher<Iterable<T>> allOfThem(final Matcher<T> matcher) {
        return new BaseMatcher<Iterable<T>>() {
            @Override
            public boolean matches(Object item) {
                if (item instanceof Iterable) {
                    Iterable iterable  = ((Iterable) item);
                    for (Object o : iterable){
                        if (!matcher.matches(o)) return false;
                    }
                    return true;
                }
                throw new AssertionError("Iterator types do not match!");
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("all items matched ");
                description.appendDescriptionOf(matcher);
            }
        };
    }


    /**
     * Matches that at least one item of provided matcher to source. If all fails this fails.
     */
    public static <T> Matcher<Iterable<T>> atLeastOne(final Matcher<T> matcher) {
        return new BaseMatcher<Iterable<T>>() {
            @Override
            public boolean matches(Object item) {
                if (item instanceof Iterable) {
                    Iterable iterable  = ((Iterable) item);
                    for (Object o : iterable){
                        if (matcher.matches(o)) return true;
                    }
                    return false;
                }
                throw new AssertionError("Iterator types do not match!");
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("at least one matched ");
                description.appendDescriptionOf(matcher);
            }
        };
    }
}
