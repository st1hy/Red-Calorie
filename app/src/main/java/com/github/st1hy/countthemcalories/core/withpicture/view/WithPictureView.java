package com.github.st1hy.countthemcalories.core.withpicture.view;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.rx.RxPicasso;
import com.github.st1hy.countthemcalories.core.baseview.DialogView;

import rx.Observable;

public interface WithPictureView extends DialogView {

    void openCameraAndGetPicture();

    void pickImageFromGallery();

    Observable<RxPicasso.PicassoEvent> showImage(@NonNull Uri uri);
}
