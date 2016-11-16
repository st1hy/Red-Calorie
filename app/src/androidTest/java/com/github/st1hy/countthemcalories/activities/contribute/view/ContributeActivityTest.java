package com.github.st1hy.countthemcalories.activities.contribute.view;

import android.content.Intent;
import android.net.Uri;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;

import com.github.st1hy.countthemcalories.activities.contribute.ContributeActivity;
import com.github.st1hy.countthemcalories.rules.ApplicationComponentRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasData;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ContributeActivityTest {

    private final ApplicationComponentRule componentRule = new ApplicationComponentRule(getTargetContext());
    public final IntentsTestRule<ContributeActivity> main = new IntentsTestRule<>(ContributeActivity.class, true, true);

    @Rule
    public final TestRule rule = RuleChain.outerRule(componentRule).around(main);

    @Test
    public void testLink() throws Exception {
        final String link = "https://github.com/st1hy/Count-Them-Calories";
        onView(withText(link)).perform(click());
        intended(allOf(hasAction(Intent.ACTION_VIEW), hasData(Uri.parse(link))));
        UiDevice.getInstance(getInstrumentation()).pressBack();
    }
}
