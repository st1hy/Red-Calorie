package com.github.st1hy.countthemcalories.core.picture;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.BasicLifecycle;

public interface PicturePresenter extends BasicLifecycle {

    void loadImageUri(@NonNull Uri uri);
}
