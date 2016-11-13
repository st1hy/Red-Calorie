package com.github.st1hy.countthemcalories.activities.settings.view;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.settings.inject.DaggerSettingsActivityComponent;
import com.github.st1hy.countthemcalories.activities.settings.inject.SettingsActivityComponent;
import com.github.st1hy.countthemcalories.activities.settings.inject.SettingsActivityModule;
import com.github.st1hy.countthemcalories.core.baseview.BaseActivity;
import com.github.st1hy.countthemcalories.core.drawer.DrawerActivity;
import com.github.st1hy.countthemcalories.core.drawer.DrawerPresenter;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class SettingsActivity extends BaseActivity {

    SettingsActivityComponent component;
    @Inject
    DrawerPresenter drawerPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getComponent().inject(this);
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

    @Override
    protected void onStart() {
        super.onStart();
        drawerPresenter.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        drawerPresenter.onStop();
    }
}
