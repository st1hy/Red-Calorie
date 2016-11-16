package com.github.st1hy.countthemcalories.activities.settings;

import android.os.Bundle;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.settings.fragment.SettingsFragment;
import com.github.st1hy.countthemcalories.inject.activities.settings.SettingsActivityModule;
import com.github.st1hy.countthemcalories.core.baseview.BaseActivity;
import com.github.st1hy.countthemcalories.core.drawer.DrawerPresenter;

import javax.inject.Inject;

public class SettingsActivity extends BaseActivity {

    @Inject
    DrawerPresenter drawerPresenter;
    @Inject
    SettingsFragment settingsFragment; //injects component

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getAppComponent().newSettingsActivityComponent(new SettingsActivityModule(this))
                .inject(this);
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
