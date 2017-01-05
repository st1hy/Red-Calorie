package com.github.st1hy.countthemcalories.core.activityresult;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.github.st1hy.countthemcalories.application.CaloriesCounterApplication;
import com.github.st1hy.countthemcalories.core.baseview.BaseFragment;

public class IntentHandlerFragment extends BaseFragment implements ActivityLauncherSubject {
    public static final String TAG = IntentHandlerFragment.class.getSimpleName();

    private RxActivityResult rxActivityResult;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        rxActivityResult = CaloriesCounterApplication.get(getActivity())
                .getComponent()
                .getRxActivityResult();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        rxActivityResult.onActivityResult(requestCode, resultCode, data);
    }
}
