package com.github.st1hy.countthemcalories.matchers;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.Iterator;

import static com.github.st1hy.countthemcalories.matchers.IterableMatchers.atLeastOne;
import static com.github.st1hy.countthemcalories.matchers.Matchers.resIdToString;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertNotNull;

public class MenuItemMatchers {
    private static Resources viewResources;

    public static Matcher<View> menuItemIsChecked(final int menuItemId) {
        return onMenuItems(atLeastOne(allOf(withMenuId(menuItemId), isChecked())));
    }

    public static Matcher<View> menuItemIsUnchecked(final int menuItemId) {
        return onMenuItems(atLeastOne(allOf(withMenuId(menuItemId), isUnchecked())));
    }

    public static Matcher<View> onMenuItems(final Matcher<Iterable<MenuItem>> matcher) {
        return new BoundedMatcher<View, NavigationView>(NavigationView.class) {
            @Override
            protected boolean matchesSafely(NavigationView item) {
                try {
                    viewResources = item.getResources();
                    return matcher.matches(new IterableMenu(item.getMenu()));
                } finally {
                    viewResources = null;
                }
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("from navigation menu items ");
                description.appendDescriptionOf(matcher);
            }
        };
    }

    public static Matcher<MenuItem> isChecked() {
        return new BoundedMatcher<MenuItem, MenuItem>(MenuItem.class) {

            @Override
            protected boolean matchesSafely(MenuItem item) {
                return item.isChecked();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is checked ");
            }
        };
    }

    public static Matcher<MenuItem> isUnchecked() {
        return new BoundedMatcher<MenuItem, MenuItem>(MenuItem.class) {

            @Override
            protected boolean matchesSafely(MenuItem item) {
                return !item.isChecked();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is unchecked ");
            }
        };
    }


    public static Matcher<MenuItem> withMenuId(final int menuItemId) {
        return new BoundedMatcher<MenuItem, MenuItem>(MenuItem.class) {

            @Override
            protected boolean matchesSafely(MenuItem item) {
                return item.getItemId() == menuItemId;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(String.format("have menu id: %s", resIdToString(viewResources, menuItemId)));
            }
        };
    }

    private static class IterableMenu implements Iterable<MenuItem> {
        private final Menu menu;

        public IterableMenu(@NonNull Menu menu) {
            assertNotNull(menu);
            this.menu = menu;
        }

        @Override
        @NonNull
        public Iterator<MenuItem> iterator() {
            return new Iterator<MenuItem>() {
                int i = 0;

                @Override
                public boolean hasNext() {
                    return i < menu.size();
                }

                @Override
                public MenuItem next() {
                    return menu.getItem(i++);
                }

                @Override
                public void remove() {
                    throw new IllegalAccessError("This iterator does not support removing any items!");
                }
            };
        }
    }
}
