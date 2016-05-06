package com.github.st1hy.countthemcalories.core.withpicture.model;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.R;

import rx.functions.Func1;

public enum ImageSource {
    GALLERY, CAMERA;

    /**
     * @param arrayItemPosition position of item in {@link R.array.add_meal_image_select_options}
     * @return ImageSource enum from that position
     * @throws IllegalArgumentException if item does not belont in the array
     */
    public static ImageSource fromItemPos(int arrayItemPosition) {
        switch (arrayItemPosition) {
            case 0: return GALLERY;
            case 1: return CAMERA;
            default: throw new IllegalArgumentException("This position doesn't match any ImageSource item!");
        }
    }

    @NonNull
    public static Func1<Integer, ImageSource> intoImageSource() {
        return new Func1<Integer, ImageSource>() {
            @Override
            public ImageSource call(Integer integer) {
                return ImageSource.fromItemPos(integer);
            }
        };
    }

}
