package com.github.st1hy.countthemcalories.activities.settings.fragment.view;

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
