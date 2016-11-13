package com.github.st1hy.countthemcalories.activities.tags.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsActivity;
import com.github.st1hy.countthemcalories.activities.tags.fragment.view.TagsFragment;
import com.github.st1hy.countthemcalories.activities.tags.inject.DaggerTagsActivityComponent;
import com.github.st1hy.countthemcalories.activities.tags.inject.TagsActivityComponent;
import com.github.st1hy.countthemcalories.activities.tags.inject.TagsActivityModule;
import com.github.st1hy.countthemcalories.core.baseview.BaseActivity;
import com.github.st1hy.countthemcalories.core.command.view.UndoDrawerActivity;
import com.github.st1hy.countthemcalories.core.drawer.DrawerPresenter;
import com.github.st1hy.countthemcalories.core.tokensearch.RxSearchable;
import com.github.st1hy.countthemcalories.core.tokensearch.TokenSearchView;
import com.github.st1hy.countthemcalories.database.Tag;
import com.jakewharton.rxbinding.view.RxView;

import org.parceler.Parcels;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

import static android.R.attr.tag;

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
