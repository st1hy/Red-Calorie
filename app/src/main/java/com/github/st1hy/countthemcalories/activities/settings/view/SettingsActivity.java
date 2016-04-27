package com.github.st1hy.countthemcalories.activities.settings.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.settings.inject.DaggerSettingsComponent;
import com.github.st1hy.countthemcalories.activities.settings.inject.SettingsComponent;
import com.github.st1hy.countthemcalories.activities.settings.inject.SettingsModule;
import com.github.st1hy.countthemcalories.activities.settings.presenter.SettingsPresenter;
import com.github.st1hy.countthemcalories.core.ui.BaseActivity;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SettingsActivity extends BaseActivity implements SettingsView {
    SettingsComponent component;

    @Inject
    SettingsPresenter presenter;

    @Bind(R.id.settings_toolbar)
    Toolbar toolbar;

    @NonNull
    protected SettingsComponent getComponent() {
        if (component == null) {
            component = DaggerSettingsComponent.builder()
                    .applicationComponent(getAppComponent())
                    .settingsModule(new SettingsModule(this))
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
    }

}
