package com.github.st1hy.countthemcalories.activities.tags;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.core.baseview.BaseActivity;
import com.github.st1hy.countthemcalories.core.drawer.DrawerPresenter;
import com.github.st1hy.countthemcalories.inject.common.ActivityModule;

import java.util.Map;

import javax.inject.Inject;

public class TagsActivity extends BaseActivity {

    public static final String ACTION_PICK_TAG = "pick tag";
    public static final String EXTRA_SELECTED_TAGS = "selected tags";
    public static final String EXTRA_TAGS = "extra tag";

    @Inject
    Map<String, Fragment> fragments; //injects  fragments
    @Inject
    DrawerPresenter drawerPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tags_activity);
        getAppComponent().newTagsActivityComponent(new ActivityModule(this))
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
