package com.github.st1hy.countthemcalories.activities.withpicture;

import com.github.st1hy.countthemcalories.R;

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
}
