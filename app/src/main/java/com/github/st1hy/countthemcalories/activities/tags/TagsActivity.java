package com.github.st1hy.countthemcalories.activities.tags;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.tags.fragment.TagsFragment;
import com.github.st1hy.countthemcalories.activities.tags.inject.DaggerTagsActivityComponent;
import com.github.st1hy.countthemcalories.inject.activities.tags.TagsActivityComponent;
import com.github.st1hy.countthemcalories.inject.activities.tags.TagsActivityModule;
import com.github.st1hy.countthemcalories.core.baseview.BaseActivity;
import com.github.st1hy.countthemcalories.core.drawer.DrawerPresenter;

import javax.inject.Inject;

public class TagsActivity extends BaseActivity {
    public static final String ACTION_PICK_TAG = "pick tag";
    public static final String EXTRA_EXCLUDE_TAG_STRING_ARRAY = "exclude tag ids";
    public static final String EXTRA_TAG = "extra tag";

    protected TagsActivityComponent component;

    @Inject
    TagsFragment fragment; //injects new fragment
    @Inject
    DrawerPresenter drawerPresenter;

    @NonNull
    protected TagsActivityComponent getComponent() {
        if (component == null) {
            component = DaggerTagsActivityComponent.builder()
                    .applicationComponent(getAppComponent())
                    .tagsActivityModule(new TagsActivityModule(this))
                    .build();
        }
        return component;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tags_activity);
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
}
