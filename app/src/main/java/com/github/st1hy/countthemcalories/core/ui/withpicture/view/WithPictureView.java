package com.github.st1hy.countthemcalories.core.ui.withpicture.view;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.ui.view.DialogView;

public interface WithPictureView extends DialogView {

    void openCameraAndGetPicture();

    void pickImageFromGallery();

    void setImageToView(@NonNull Uri uri);
}
