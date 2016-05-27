package com.github.st1hy.countthemcalories.actions;

import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.ViewActions;

public final class CTCViewActions {

    public static ViewAction betterScrollTo() {
        return ViewActions.actionWithAssertions(new NestedScrollToAction());
    }

    public static ViewAction loopMainThreadForAtLeast(int howLong) {
        return ViewActions.actionWithAssertions(new WaitAction(howLong));
    }

}
