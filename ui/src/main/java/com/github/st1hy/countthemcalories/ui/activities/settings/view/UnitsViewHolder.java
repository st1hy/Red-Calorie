package com.github.st1hy.countthemcalories.ui.activities.settings.view;

import android.support.annotation.NonNull;
import android.view.View;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.ui.inject.app.PerFragment;
import com.github.st1hy.countthemcalories.ui.inject.quantifier.view.FragmentRootView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

@PerFragment
public class UnitsViewHolder {

    @BindView(R.id.settings_unit_energy)
    View energy;
    @BindView(R.id.settings_unit_mass)
    View mass;
    @BindView(R.id.settings_unit_volume)
    View volume;
    @BindView(R.id.settings_unit_body_mass)
    View bodyMass;

    @Inject
    public UnitsViewHolder(@NonNull @FragmentRootView View root) {
        ButterKnife.bind(this, root);
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

    public View getBodyMass() {
        return bodyMass;
    }
}
