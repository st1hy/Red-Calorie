package com.github.st1hy.countthemcalories.core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.util.DisplayMetrics;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.robolectric.util.ReflectionHelpers;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UtilsTest {
    @DrawableRes
    private static final int testResId = 0xFF;
    private static final int MARSHMALLOW = Build.VERSION_CODES.M;
    private static final int LOLLIPOP = Build.VERSION_CODES.LOLLIPOP;

    @Mock
    private Drawable drawableMock;
    @Mock
    private DisplayMetrics mockedMetrics;
    @Mock
    private Resources mockedResources;
    @Mock
    private Context mockedContext;
    @Mock
    private Resources.Theme mockedTheme;

    private Utils utils = new Utils();

    @Before
    public void setup() {
        when(mockedContext.getResources()).thenReturn(mockedResources);
        when(mockedResources.getDisplayMetrics()).thenReturn(mockedMetrics);
        when(mockedContext.getTheme()).thenReturn(mockedTheme);
    }

    @Test
    public void testHasLollipop() throws Exception {
        for (int i = 1; i < LOLLIPOP; i++) {
            setBuildVersion(i);
            assertFalse(utils.hasLollipop());
        }
        for (int i = LOLLIPOP; i < 100; i++) {
            setBuildVersion(i);
            assertTrue(utils.hasLollipop());
        }
    }

    @Test
    public void testHasMarshmallow() throws Exception {
        for (int i = 1; i < MARSHMALLOW; i++) {
            setBuildVersion(i);
            assertFalse(utils.hasMarshmallow());
        }
        for (int i = MARSHMALLOW; i < 100; i++) {
            setBuildVersion(i);
            assertTrue(utils.hasMarshmallow());
        }
    }

    private void setBuildVersion(int version) {
        ReflectionHelpers.setStaticField(Build.VERSION.class, "SDK_INT", version);
    }

    @SuppressLint("NewApi")
    @Test
    public void testGetDrawableApi21() throws Exception {
        setBuildVersion(LOLLIPOP);
        when(mockedResources.getDrawableForDensity(anyInt(), anyInt(), any(Resources.Theme.class))).thenReturn(drawableMock);

        Drawable drawable = utils.getDrawable(mockedContext, testResId);
        assertEquals(drawableMock, drawable);

        verify(mockedContext).getResources();
        verify(mockedResources).getDisplayMetrics();
        verify(mockedContext).getTheme();
        verify(mockedResources).getDrawableForDensity(eq(testResId), anyInt(), any(Resources.Theme.class));
        verifyNoMoreInteractions(mockedContext, mockedResources, drawableMock);
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    @Test
    public void testGetDrawable() {
        setBuildVersion(LOLLIPOP - 1);
        when(mockedResources.getDrawableForDensity(anyInt(), anyInt())).thenReturn(drawableMock);

        Drawable drawable = utils.getDrawable(mockedContext, testResId);

        verify(mockedContext).getResources();
        verify(mockedResources).getDisplayMetrics();
        verify(mockedResources).getDrawableForDensity(eq(testResId), anyInt());
        verifyNoMoreInteractions(mockedContext, mockedResources, drawableMock);

        assertEquals(drawableMock, drawable);
    }


    @SuppressWarnings("deprecation")
    @Test(expected = Resources.NotFoundException.class)
    public void testGetDrawableNoResource() throws Exception {
        setBuildVersion(LOLLIPOP - 1);
        when(mockedResources.getDrawableForDensity(anyInt(), anyInt()))
                .thenThrow(new Resources.NotFoundException());

        utils.getDrawable(mockedContext, testResId);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testGetDrawableSafely() throws Exception {
        setBuildVersion(LOLLIPOP - 1);
        when(mockedResources.getDrawableForDensity(anyInt(), anyInt())).thenReturn(drawableMock);

        Drawable drawable = utils.getDrawableSafely(mockedContext, testResId);
        assertEquals(drawableMock, drawable);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testGetDrawableSafelyNoResource() throws Exception {
        setBuildVersion(LOLLIPOP - 1);
        when(mockedResources.getDrawableForDensity(anyInt(), anyInt()))
                .thenThrow(new Resources.NotFoundException());

        Drawable drawable = utils.getDrawableSafely(mockedContext, testResId);
        assertNull(drawable);
    }
}