package com.github.st1hy.countthemcalories.activities.settings.view;

import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.activities.settings.view.holder.SelectUnitViewHolder;
import com.github.st1hy.countthemcalories.core.baseview.DialogView;
import com.github.st1hy.countthemcalories.core.drawer.view.DrawerView;

public interface SettingsView extends DialogView, DrawerView {

    @NonNull
    SelectUnitViewHolder getEnergyHolder();
    @NonNull
    SelectUnitViewHolder getMassHolder();
    @NonNull
    SelectUnitViewHolder getVolumeHolder();
}
