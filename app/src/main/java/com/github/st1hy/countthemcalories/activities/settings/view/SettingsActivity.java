package com.github.st1hy.countthemcalories.activities.settings.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.settings.inject.DaggerSettingsActivityComponent;
import com.github.st1hy.countthemcalories.activities.settings.inject.SettingsActivityComponent;
import com.github.st1hy.countthemcalories.activities.settings.inject.SettingsActivityModule;
import com.github.st1hy.countthemcalories.activities.settings.presenter.SettingsPresenter;
import com.github.st1hy.countthemcalories.core.view.BaseActivity;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SettingsActivity extends BaseActivity implements SettingsView {
    SettingsActivityComponent component;

    @Inject
    SettingsPresenter presenter;

    @Bind(R.id.settings_toolbar)
    Toolbar toolbar;
    @Bind(R.id.settings_energy_density_liquid)
    View liquidUnitView;
    @Bind(R.id.settings_drinks_unit)
    TextView liquidUnitText;
    @Bind(R.id.settings_energy_density_solid)
    View solidUnitView;
    @Bind(R.id.settings_meals_unit)
    TextView solidUnitText;

    @NonNull
    protected SettingsActivityComponent getComponent() {
        if (component == null) {
            component = DaggerSettingsActivityComponent.builder()
                    .applicationComponent(getAppComponent())
                    .settingsActivityModule(new SettingsActivityModule(this))
                    .build();
        }
        return component;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        ButterKnife.bind(this);
        getComponent().inject(this);
        setSupportActionBar(toolbar);
        assertNotNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        liquidUnitView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onLiquidUnitSettingsClicked();
            }
        });
        solidUnitView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onSolidUnitSettingsClicked();
            }
        });
        presenter.showCurrentUnits();
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.onStop();
    }

    @Override
    public void setLiquidUnit(String unitName) {
        liquidUnitText.setText(unitName);
    }

    @Override
    public void setSolidUnit(String unitName) {
        solidUnitText.setText(unitName);
    }
}
