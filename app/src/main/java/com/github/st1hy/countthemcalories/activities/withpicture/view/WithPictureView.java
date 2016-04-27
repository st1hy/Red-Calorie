package com.github.st1hy.countthemcalories.activities.withpicture.view;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.ui.view.BaseView;

public interface WithPictureView extends BaseView {

    void showSelectImageInputDialog();

    void openCameraAndGetPicture();

    void pickImageFromGallery();

    void setImageToView(@NonNull Uri uri);
}
