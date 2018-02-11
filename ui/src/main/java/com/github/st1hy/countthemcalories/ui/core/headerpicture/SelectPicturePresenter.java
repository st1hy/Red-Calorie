package com.github.st1hy.countthemcalories.ui.core.headerpicture;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.ui.core.BasicLifecycle;

public interface SelectPicturePresenter extends BasicLifecycle {

    void loadImageUri(@NonNull Uri uri);

}
