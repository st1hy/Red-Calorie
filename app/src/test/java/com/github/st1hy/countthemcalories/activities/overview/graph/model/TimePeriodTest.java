package com.github.st1hy.countthemcalories.activities.overview.graph.model;

import org.junit.Test;

import static com.github.st1hy.countthemcalories.database.rx.timeperiod.TimePeriod.median;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class TimePeriodTest {

    @Test
    public void testMedian() throws Exception {
        assertThat(median(arr()), equalTo(0f));
        assertThat(median(arr(2.552f)), equalTo(2.552f));
        assertThat(median(arr(3f, 4.f)), equalTo(3.5f));
        assertThat(median(arr(-2f, 1f, 2312313f)), equalTo(1f));
    }

    @Test
    public void testMedian2() throws Exception {
        assertThat(median(arr(), 0, 0), equalTo(0f));
        assertThat(median(arr(2.552f), 0, 1), equalTo(2.552f));
        assertThat(median(arr(2.552f), 1, 1), equalTo(0f));
        assertThat(median(arr(3f, 4.f), 1, 2), equalTo(4f));
        assertThat(median(arr(3f, 4.f), 2, 2), equalTo(0f));
        assertThat(median(arr(-2f, 1f, 2312313f), 0, 3), equalTo(1f));
        assertThat(median(arr(-2f, 1f, 2312313f), 1, 3), equalTo(1156157f));
        assertThat(median(arr(-2f, 1f, 2312313f), 1, 2), equalTo(1f));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMedian3() throws Exception {
        median(arr(), -1, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMedian4() throws Exception {
        median(arr(), 2, 1);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testMedian5() throws Exception {
        median(arr(), 5, 7);
    }

    private static float[] arr(float... a) {
        return a;
    }
}