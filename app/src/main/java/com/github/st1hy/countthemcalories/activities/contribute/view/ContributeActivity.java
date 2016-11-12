package com.github.st1hy.countthemcalories.activities.contribute.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.contribute.inject.ContributeActivityComponent;
import com.github.st1hy.countthemcalories.activities.contribute.inject.ContributeActivityModule;
import com.github.st1hy.countthemcalories.activities.contribute.inject.DaggerContributeActivityComponent;
import com.github.st1hy.countthemcalories.core.baseview.BaseActivity;
import com.github.st1hy.countthemcalories.core.drawer.DrawerActivity;
import com.github.st1hy.countthemcalories.core.drawer.DrawerPresenter;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class ContributeActivity extends BaseActivity {

    ContributeActivityComponent component;

    @Inject
    DrawerPresenter drawerPresenter;

    @NonNull
    protected ContributeActivityComponent getComponent() {
        if (component == null) {
            component = DaggerContributeActivityComponent.builder()
                    .contributeActivityModule(new ContributeActivityModule(this))
                    .build();
        }
        return component;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contribute_activity);
        getComponent().inject(this);
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
