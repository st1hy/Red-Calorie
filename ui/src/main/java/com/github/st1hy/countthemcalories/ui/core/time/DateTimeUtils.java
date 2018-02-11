package com.github.st1hy.countthemcalories.ui.core.time;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;

import javax.inject.Inject;

public class DateTimeUtils {

    @Inject
    public DateTimeUtils() {
    }

    public long getMillisToNextFullMinute() {
        return getMillisToNextFullMinute(DateTime.now());
    }

    public long getMillisToNextFullMinute(DateTime time) {
        MutableDateTime nextMinute = time.toMutableDateTime();
        final long nowMillis = nextMinute.getMillis();
        nextMinute.setMillis(nowMillis + 60_000L);
        nextMinute.setMillisOfSecond(0);
        nextMinute.setSecondOfMinute(0);
        return nextMinute.getMillis() - nowMillis;
    }
}
