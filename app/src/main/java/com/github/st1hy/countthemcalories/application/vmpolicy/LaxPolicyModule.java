package com.github.st1hy.countthemcalories.application.vmpolicy;

import android.os.StrictMode;

public class LaxPolicyModule extends PolicyModule {

    @Override
    public StrictMode.VmPolicy provideVmPolicy() {
        return StrictMode.VmPolicy.LAX;
    }

    @Override
    public StrictMode.ThreadPolicy provideThreadPolicy() {
        return StrictMode.ThreadPolicy.LAX;
    }
}
