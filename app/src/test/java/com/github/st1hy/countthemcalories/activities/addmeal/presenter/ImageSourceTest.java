package com.github.st1hy.countthemcalories.activities.addmeal.presenter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static com.github.st1hy.countthemcalories.activities.addmeal.presenter.ImageSource.CAMERA;
import static com.github.st1hy.countthemcalories.activities.addmeal.presenter.ImageSource.GALLERY;
import static com.github.st1hy.countthemcalories.activities.addmeal.presenter.ImageSource.fromItemPos;
import static junit.framework.Assert.assertEquals;

@RunWith(JUnit4.class)
public class ImageSourceTest {


    @Test
    public void testFromItemPos() {
        assertEquals(fromItemPos(0), GALLERY);
        assertEquals(fromItemPos(1), CAMERA);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalPosition() {
        fromItemPos(-1);
    }
}