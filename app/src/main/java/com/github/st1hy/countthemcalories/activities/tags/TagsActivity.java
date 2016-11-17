package com.github.st1hy.countthemcalories.activities.tags;

import android.os.Bundle;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.tags.fragment.TagsFragment;
import com.github.st1hy.countthemcalories.core.baseview.BaseActivity;
import com.github.st1hy.countthemcalories.core.drawer.DrawerPresenter;
import com.github.st1hy.countthemcalories.inject.activities.tags.TagsActivityModule;

import javax.inject.Inject;

public class TagsActivity extends BaseActivity {
    public static final String ACTION_PICK_TAG = "pick tag";
    public static final String EXTRA_EXCLUDE_TAG_STRING_ARRAY = "exclude tag ids";
    public static final String EXTRA_TAG = "extra tag";

    @Inject
    TagsFragment fragment; //injects new fragment
    @Inject
    DrawerPresenter drawerPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tags_activity);
        getAppComponent().newTagsActivityComponent(new TagsActivityModule(this))
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
}
