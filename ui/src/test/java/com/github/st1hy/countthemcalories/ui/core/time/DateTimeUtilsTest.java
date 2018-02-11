package com.github.st1hy.countthemcalories.ui.core.time;

import com.github.st1hy.countthemcalories.ui.core.time.DateTimeUtils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class DateTimeUtilsTest {

    @Before
    public void setUp() {
        DateTimeZone.setDefault(DateTimeZone.UTC);
    }

    @Test
    public void getMillisToNextFullMinute() throws Exception {
        DateTimeUtils dateTimeUtils = new DateTimeUtils();
        assertThat(dateTimeUtils.getMillisToNextFullMinute(DateTime.now().withTime(0,0,0,0)),
                equalTo(60_000L));
        assertThat(dateTimeUtils.getMillisToNextFullMinute(DateTime.now().withTime(23,59,58,482)),
                equalTo(1518L));
    }

}