package com.github.st1hy.countthemcalories.activities.contribute.view;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.contribute.inject.ContributeActivityModule;
import com.github.st1hy.countthemcalories.core.baseview.BaseActivity;
import com.github.st1hy.countthemcalories.core.drawer.DrawerPresenter;

import javax.inject.Inject;

public class ContributeActivity extends BaseActivity {

    @Inject
    DrawerPresenter drawerPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contribute_activity);
        getAppComponent().newContributeActivityComponent(new ContributeActivityModule(this))
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
