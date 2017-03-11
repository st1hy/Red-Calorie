package com.github.st1hy.countthemcalories.activities.settings.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.settings.fragment.inject.SettingsFragmentComponentFactory;
import com.github.st1hy.countthemcalories.activities.settings.fragment.presenter.SettingsPresenter;
import com.github.st1hy.countthemcalories.core.baseview.BaseFragment;
import com.github.st1hy.countthemcalories.inject.common.FragmentModule;

import javax.inject.Inject;

public class SettingsFragment extends BaseFragment {

    private SettingsFragmentComponentFactory componentFactory;

    @Inject
    SettingsPresenter presenter;

    public void setComponentFactory(@NonNull SettingsFragmentComponentFactory componentFactory) {
        this.componentFactory = componentFactory;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings_content, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        componentFactory.newSettingsFragmentComponent(new FragmentModule(this, savedInstanceState))
                .inject(this);
        componentFactory = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.onStop();
    }

}
