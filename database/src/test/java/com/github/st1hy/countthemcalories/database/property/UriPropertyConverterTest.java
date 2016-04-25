package com.github.st1hy.countthemcalories.database.property;


import android.net.Uri;

import com.github.st1hy.countthemcalories.database.BuildConfig;
import com.github.st1hy.countthemcalories.database.application.DatabaseApplication;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, application = DatabaseApplication.class)
public class UriPropertyConverterTest {
    private final UriPropertyConverter uriPropertyConverter = new UriPropertyConverter();

    @Test
    public void testConversion() throws Exception {
        final Uri uri = Uri.parse("http://example.org/id?=example&sdd=1");
        assertThat(uri, equalTo(uriPropertyConverter.convertToEntityProperty(uriPropertyConverter.convertToDatabaseValue(uri))));
    }

    @Test
    public void testNullValues() throws Exception {
        assertThat(uriPropertyConverter.convertToDatabaseValue(null), nullValue());
        assertThat(uriPropertyConverter.convertToEntityProperty(null), nullValue());

    }
}