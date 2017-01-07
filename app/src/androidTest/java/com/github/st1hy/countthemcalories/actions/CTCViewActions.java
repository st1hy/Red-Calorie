package com.github.st1hy.countthemcalories.actions;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.view.View;
import android.widget.TimePicker;

import org.hamcrest.Matcher;

public final class CTCViewActions {

    public static ViewAction betterScrollTo() {
        return ViewActions.actionWithAssertions(new NestedScrollToAction());
    }

    public static ViewAction loopMainThreadForAtLeast(int howLong) {
        return ViewActions.actionWithAssertions(new WaitAction(howLong));
    }

    public static ViewAction setTime(final int hour, final int minute) {
        return new ViewAction() {
            @Override
            public void perform(UiController uiController, View view) {
                TimePicker tp = (TimePicker) view;
                tp.setCurrentHour(hour);
                tp.setCurrentMinute(minute);
            }
            @Override
            public String getDescription() {
                return "Set the passed time into the TimePicker";
            }
            @Override
            public Matcher<View> getConstraints() {
                return ViewMatchers.isAssignableFrom(TimePicker.class);
            }
        };
    }
}
