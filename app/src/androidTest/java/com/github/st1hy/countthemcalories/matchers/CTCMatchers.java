package com.github.st1hy.countthemcalories.matchers;

import android.content.Intent;

import org.hamcrest.Matcher;

import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;

public class CTCMatchers {

    public static final Matcher<Intent> galleryIntentMatcher = hasAction(Intent.ACTION_CHOOSER);
}
