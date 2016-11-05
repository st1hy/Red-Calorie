package com.github.st1hy.countthemcalories.activities.settings.fragment.view;

import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.settings.fragment.inject.DaggerSettingsFragmentComponent;
import com.github.st1hy.countthemcalories.activities.settings.fragment.inject.SettingsFragmentComponent;
import com.github.st1hy.countthemcalories.activities.settings.fragment.inject.SettingsFragmentModule;
import com.github.st1hy.countthemcalories.activities.settings.fragment.presenter.SettingsPresenter;
import com.github.st1hy.countthemcalories.core.baseview.BaseFragment;
import com.github.st1hy.countthemcalories.core.dialog.DialogView;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;

public class SettingsFragment extends BaseFragment implements SettingsView {

    SettingsFragmentComponent component;

    @Inject
    SettingsPresenter presenter;
    @Inject
    DialogView dialogView;

    @Inject @Named("energy")
    SelectUnitViewHolder energyHolder;
    @Inject @Named("mass") SelectUnitViewHolder massHolder;
    @Inject @Named("volume") SelectUnitViewHolder volumeHolder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings_content, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getComponent().inject(this);
    }

    @NonNull
    protected SettingsFragmentComponent getComponent() {
        if (component == null) {
            component = DaggerSettingsFragmentComponent.builder()
                    .applicationComponent(getAppComponent())
                    .settingsFragmentModule(new SettingsFragmentModule(this))
                    .build();
        }
        return component;
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

    @NonNull
    public SelectUnitViewHolder getEnergyHolder() {
        return energyHolder;
    }

    @NonNull
    public SelectUnitViewHolder getMassHolder() {
        return massHolder;
    }

    @NonNull
    public SelectUnitViewHolder getVolumeHolder() {
        return volumeHolder;
    }

    @Override
    public Observable<Integer> showAlertDialog(@StringRes int titleRes, @ArrayRes int optionsRes) {
        return dialogView.showAlertDialog(titleRes, optionsRes);
    }

    @Override
    public Observable<Integer> showAlertDialog(@StringRes int titleRes, CharSequence[] options) {
        return dialogView.showAlertDialog(titleRes, options);
    }
}
