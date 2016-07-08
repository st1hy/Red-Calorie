package com.github.st1hy.countthemcalories.activities.settings.view;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.settings.inject.DaggerSettingsActivityComponent;
import com.github.st1hy.countthemcalories.activities.settings.inject.SettingsActivityComponent;
import com.github.st1hy.countthemcalories.activities.settings.inject.SettingsActivityModule;
import com.github.st1hy.countthemcalories.core.drawer.view.DrawerActivity;

import butterknife.ButterKnife;

public class SettingsActivity extends DrawerActivity {

    SettingsActivityComponent component;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        ButterKnife.bind(this);
        getComponent().inject(this);
        onBind();
    }

    @NonNull
    private SettingsActivityComponent getComponent() {
        if (component == null) {
            component = DaggerSettingsActivityComponent.builder()
                    .settingsActivityModule(new SettingsActivityModule(this))
                    .build();
        }
        return component;
    }

}
