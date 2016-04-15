package com.github.st1hy.countthemcalories;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class OverviewActivityTest {

    @Rule
    public final ActivityTestRule<OverviewActivity> main = new ActivityTestRule<>(OverviewActivity.class);


    @Test
    public void testActivityStart() {
        Assert.assertNotNull(main.getActivity());
    }


}