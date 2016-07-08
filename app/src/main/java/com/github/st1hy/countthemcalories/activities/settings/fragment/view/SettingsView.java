package com.github.st1hy.countthemcalories.activities.settings.fragment.view;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.core.baseview.DialogView;

public interface SettingsView extends DialogView {

    @NonNull
    SelectUnitViewHolder getEnergyHolder();
    @NonNull
    SelectUnitViewHolder getMassHolder();
    @NonNull
    SelectUnitViewHolder getVolumeHolder();
}
