package com.github.st1hy.countthemcalories.ui.activities.tags.model;

import android.graphics.Color;
import android.support.annotation.ColorInt;

import java.util.Random;

import javax.inject.Inject;

public class ColorGenerator {

    private static final long seed = 0x3358f291L;

    private static final int alpha = 0xFF;
    private static final float saturation = 0.8f; // 0..1
    private static final float value = 0.7f; // 0..1
    private final Random random;

    @Inject
    ColorGenerator() {
        random = new Random(seed);
    }

    /**
     * @return pseudo random color value the same for specified id
     */
    @ColorInt
    public int getColorFor(long id) {
        long seed = ColorGenerator.seed + id;
        random.setSeed(seed);
        float hue = random.nextInt(360); //0..360
        return Color.HSVToColor(alpha, new float[]{hue, saturation, value});
    }
}
