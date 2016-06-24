package com.github.st1hy.countthemcalories.activities.contribute.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.contribute.inject.ContributeActivityComponent;
import com.github.st1hy.countthemcalories.activities.contribute.inject.ContributeActivityModule;
import com.github.st1hy.countthemcalories.activities.contribute.inject.DaggerContributeActivityComponent;
import com.github.st1hy.countthemcalories.core.drawer.view.DrawerActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContributeActivity extends DrawerActivity implements ContributeView {

    ContributeActivityComponent component;

    @BindView(R.id.contribute_link)
    TextView link;

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
        ButterKnife.bind(this);
        getComponent().inject(this);
        onBind();

        link.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
