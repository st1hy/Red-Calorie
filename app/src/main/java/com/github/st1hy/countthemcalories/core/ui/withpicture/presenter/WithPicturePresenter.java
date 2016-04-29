package com.github.st1hy.countthemcalories.core.ui.withpicture.presenter;

import android.net.Uri;
import android.support.annotation.NonNull;

public interface WithPicturePresenter {

    void onImageClicked();

    void onImageReceived(@NonNull Uri data);

    void onStop();
}
