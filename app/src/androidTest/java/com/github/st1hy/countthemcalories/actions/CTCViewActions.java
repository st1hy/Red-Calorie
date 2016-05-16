package com.github.st1hy.countthemcalories.actions;

import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.ViewActions;

public class CTCViewActions {

    public static ViewAction betterScrollTo() {
        return ViewActions.actionWithAssertions(new NestedScrollToAction());
    }

}
