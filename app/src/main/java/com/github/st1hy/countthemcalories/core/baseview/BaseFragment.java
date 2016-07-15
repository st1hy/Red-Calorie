package com.github.st1hy.countthemcalories.core.baseview;

import android.support.v4.app.Fragment;

import com.github.st1hy.countthemcalories.application.CaloriesCounterApplication;
import com.github.st1hy.countthemcalories.application.inject.ApplicationComponent;

public class BaseFragment extends Fragment {

    protected final ApplicationComponent getAppComponent() {
        return ((CaloriesCounterApplication) getActivity().getApplication()).getComponent();
    }
}
