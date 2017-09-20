package com.github.st1hy.countthemcalories.core.headerpicture;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.BasicLifecycle;

public interface SelectPicturePresenter extends BasicLifecycle {

    void loadImageUri(@NonNull Uri uri);

}
