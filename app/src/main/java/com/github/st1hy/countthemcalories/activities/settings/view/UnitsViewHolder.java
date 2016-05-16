package com.github.st1hy.countthemcalories.activities.settings.view;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;

import com.github.st1hy.countthemcalories.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UnitsViewHolder {

    @BindView(R.id.settings_unit_energy)
    View energy;
    @BindView(R.id.settings_unit_mass)
    View mass;
    @BindView(R.id.settings_unit_volume)
    View volume;

    public UnitsViewHolder(@NonNull Activity activity) {
        ButterKnife.bind(this, activity);
    }

    @NonNull
    public View getEnergy() {
        return energy;
    }

    @NonNull
    public View getMass() {
        return mass;
    }

    @NonNull
    public View getVolume() {
        return volume;
    }
}
