package com.github.st1hy.countthemcalories.core.headerpicture;

import android.support.annotation.NonNull;

import rx.functions.Func1;

public enum ImageSource {
    GALLERY, CAMERA, REMOVE_SOURCE;

    private static final Func1<Integer, ImageSource> INTO_IMAGE_SOURCE = ImageSource::fromItemPos;

    /**
     * @param arrayItemPosition position of item in R.array.add_meal_image_select_options
     * @return ImageSource enum from that position
     * @throws IllegalArgumentException if item does not belont in the array
     */
    public static ImageSource fromItemPos(int arrayItemPosition) {
        switch (arrayItemPosition) {
            case 0:
                return GALLERY;
            case 1:
                return CAMERA;
            case 2:
                return REMOVE_SOURCE;
            default:
                throw new IllegalArgumentException("This position doesn't match any ImageSource item!");
        }
    }

    @NonNull
    public static Func1<Integer, ImageSource> intoImageSource() {
        return INTO_IMAGE_SOURCE;
    }

}
