package com.github.st1hy.countthemcalories.activities.overview.presenter;

import android.content.Intent;
import android.support.annotation.Nullable;

import com.github.st1hy.countthemcalories.core.drawer.presenter.DrawerPresenter;

public interface OverviewPresenter extends DrawerPresenter {

    boolean onActivityResult(int requestCode, int resultCode, @Nullable Intent data);
}
