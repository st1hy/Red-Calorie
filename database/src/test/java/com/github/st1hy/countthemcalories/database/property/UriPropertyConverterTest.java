package com.github.st1hy.countthemcalories.database.property;


import android.net.Uri;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Uri.class})
public class UriPropertyConverterTest {
    private final UriPropertyConverter uriPropertyConverter = new UriPropertyConverter();

    @Mock
    private Uri uri;

    @Before
    public void setUp() throws Exception {
        final String uriString = "http://example.org/id?=example&sdd=1";
        when(uri.toString()).thenReturn(uriString);
        PowerMockito.mockStatic(Uri.class);
        PowerMockito.when(Uri.class, "parse", uriString).thenReturn(uri);
    }

    @Test
    public void testConversion() throws Exception {
        String databaseString = uriPropertyConverter.convertToDatabaseValue(uri);
        Uri recreatedUri = uriPropertyConverter.convertToEntityProperty(databaseString);
        assertThat(uri, equalTo(recreatedUri));
    }

    @Test
    public void testNullValues() throws Exception {
        assertThat(uriPropertyConverter.convertToDatabaseValue(null), nullValue());
        assertThat(uriPropertyConverter.convertToEntityProperty(null), nullValue());

    }
}