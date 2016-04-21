package com.github.st1hy.countthemcalories.activities.addmeal.presenter;

public enum ImageSource {
    GALLERY, CAMERA;

    public static ImageSource fromItemPos(int arrayItemPosition) {
        switch (arrayItemPosition) {
            case 0: return GALLERY;
            case 1: return CAMERA;
            default: throw new UnsupportedOperationException();
        }
    }
}
