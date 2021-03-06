package com.github.st1hy.countthemcalories.core.meals;

import com.github.st1hy.countthemcalories.R;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class DefaultNameEnTest {

    DefaultNameEn defaultNameEn;

    @Before
    public void setUp() {
        DateTimeZone.setDefault(DateTimeZone.UTC);
        defaultNameEn = new DefaultNameEn();
    }

    @Test
    public void matchDate() throws Exception {
        assertThat(defaultNameEn.matchDate(new DateTime().withTime(8,0,0,0)),
                equalTo(R.string.add_meal_name_breakfast));
        assertThat(defaultNameEn.matchDate(new DateTime().withTime(12,0,0,0)),
                equalTo(R.string.add_meal_name_lunch));
        assertThat(defaultNameEn.matchDate(new DateTime().withTime(16,0,0,0)),
                equalTo(R.string.add_meal_name_dinner));
        assertThat(defaultNameEn.matchDate(new DateTime().withTime(0,0,0,0)),
                equalTo(R.string.add_meal_name_dinner));
    }

}