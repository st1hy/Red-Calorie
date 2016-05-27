package com.github.st1hy.countthemcalories.actions;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.view.View;

import org.hamcrest.Matcher;

import static org.hamcrest.Matchers.any;

public class WaitAction implements ViewAction {
    private final int time;

    public WaitAction(int time) {
        this.time = time;
    }

    @Override
    public Matcher<View> getConstraints() {
        return any(View.class);
    }

    @Override
    public String getDescription() {
        return "waits specific amount of time";
    }

    @Override
    public void perform(UiController uiController, View view) {
        uiController.loopMainThreadForAtLeast(time);
    }
}
