package com.github.st1hy.countthemcalories.core.activityresult;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

public interface ActivityLauncherSubject {

    void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options);
}
