package com.github.st1hy.countthemcalories.core.tokensearch;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.GeneralClickAction;
import android.support.test.espresso.action.GeneralLocation;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Tap;
import android.view.View;

import org.hamcrest.Matcher;

import static android.support.test.espresso.action.ViewActions.actionWithAssertions;

public class TokenSearchViewActions {

    public static ViewAction clickExpand() {
        return actionWithAssertions(new ExpandClickAction());
    }

    private static class ExpandClickAction implements ViewAction {

        private final ViewAction click = new GeneralClickAction(Tap.SINGLE, GeneralLocation.VISIBLE_CENTER, Press.FINGER);

        @Override
        public Matcher<View> getConstraints() {
            return click.getConstraints();
        }

        @Override
        public String getDescription() {
            return click.getDescription();
        }

        @Override
        public void perform(UiController uiController, View view) {
            if (view instanceof TokenSearchView) {
                click.perform(uiController, ((TokenSearchView) view).expand);
            } else {
                click.perform(uiController, view);
            }
        }
    }

}
