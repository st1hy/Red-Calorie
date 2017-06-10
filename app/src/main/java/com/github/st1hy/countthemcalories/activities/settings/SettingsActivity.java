package com.github.st1hy.countthemcalories.activities.settings;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.core.baseview.BaseActivity;
import com.github.st1hy.countthemcalories.core.drawer.DrawerPresenter;
import com.github.st1hy.countthemcalories.inject.common.ActivityModule;

import java.util.Map;

import javax.inject.Inject;

public class SettingsActivity extends BaseActivity {

    @Inject
    DrawerPresenter drawerPresenter;
    @Inject
    Map<String, Fragment> fragments; //injects component

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getAppComponent().newSettingsActivityComponent(new ActivityModule(this))
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

    @Override
    public void onBackPressed() {
        if (!drawerPresenter.onBackPressed()) super.onBackPressed();
    }
}
