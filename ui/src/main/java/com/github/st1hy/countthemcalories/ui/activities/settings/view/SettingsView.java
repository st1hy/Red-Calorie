package com.github.st1hy.countthemcalories.ui.activities.settings.view;

import android.support.annotation.NonNull;

public interface SettingsView {

    @NonNull
    SelectUnitViewHolder getEnergyHolder();

    @NonNull
    SelectUnitViewHolder getMassHolder();

    @NonNull
    SelectUnitViewHolder getVolumeHolder();

    @NonNull
    SelectUnitViewHolder getBodyMassHolder();
}
