package com.github.st1hy.countthemcalories.activities.overview;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.overview.meals.inject.OverviewFragmentComponentFactory;
import com.github.st1hy.countthemcalories.activities.overview.presenter.OverviewPresenter;
import com.github.st1hy.countthemcalories.activities.overview.presenter.OverviewStateSaver;
import com.github.st1hy.countthemcalories.core.baseview.BaseActivity;
import com.github.st1hy.countthemcalories.core.drawer.DrawerPresenter;
import com.github.st1hy.countthemcalories.inject.common.ActivityModule;

import javax.inject.Inject;

public class OverviewActivity extends BaseActivity {

    public static final String EXTRA_JUMP_TO_DATE = "extra jump to date";
    @Inject
    OverviewPresenter presenter;
    @Inject
    OverviewFragmentComponentFactory mealsFragmentComponentFactory;
    @Inject
    OverviewStateSaver saver;
    @Inject
    DrawerPresenter drawerPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overview_activity);
        getAppComponent().newOverviewActivityComponent(new ActivityModule(this, savedInstanceState))
                .inject(this);
        setTitle("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return presenter.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saver.onSaveState(outState);
    }

    @NonNull
    public OverviewFragmentComponentFactory getMealsFragmentComponentFactory() {
        return mealsFragmentComponentFactory;
    }

    @Override
    public void onBackPressed() {
        if (drawerPresenter.onBackPressed()) super.onBackPressed();
    }
}
