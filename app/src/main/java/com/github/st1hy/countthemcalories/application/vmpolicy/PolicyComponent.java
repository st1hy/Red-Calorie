package com.github.st1hy.countthemcalories.application.vmpolicy;

import android.os.StrictMode;

import dagger.Component;

@Component(modules = PolicyModule.class)
public interface PolicyComponent {

    StrictMode.VmPolicy getVmPolicy();

    StrictMode.ThreadPolicy getThreadPolicy();
}
