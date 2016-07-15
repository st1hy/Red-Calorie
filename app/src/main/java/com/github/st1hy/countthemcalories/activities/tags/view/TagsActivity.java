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
import com.github.st1hy.countthemcalories.core.command.view.UndoDrawerActivity;
import com.github.st1hy.countthemcalories.core.tokensearch.RxSearchable;
import com.github.st1hy.countthemcalories.core.tokensearch.TokenSearchView;
import com.jakewharton.rxbinding.view.RxView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

public class TagsActivity extends UndoDrawerActivity implements TagsScreen {
    public static final String ACTION_PICK_TAG = "pick tag";
    public static final String EXTRA_EXCLUDE_TAG_STRING_ARRAY = "exclude tag id";
    public static final String EXTRA_TAG_ID = "extra tag id";
    public static final String EXTRA_TAG_NAME = "extra tag name";

    protected TagsActivityComponent component;

    @BindView(R.id.tags_root)
    CoordinatorLayout root;
    @BindView(R.id.tags_search_view)
    TokenSearchView searchView;
    @BindView(R.id.tags_add_new)
    FloatingActionButton fab;

    @Inject
    TagsFragment fragment;

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
        ButterKnife.bind(this);
        getComponent().inject(this);
        onBind();
    }

    @Override
    protected void onBind() {
        super.onBind();
        searchView.getSearchTextView().setSplitChar(new char[]{0xAD});
    }

    @NonNull
    @Override
    public Observable<Void> getAddTagClickedObservable() {
        return RxView.clicks(fab);
    }


    @Override
    public void openIngredientsFilteredBy(@NonNull String tagName) {
        Intent intent = new Intent(this, IngredientsActivity.class);
        intent.putExtra(IngredientsActivity.EXTRA_TAG_FILTER_STRING, tagName);
        startActivity(intent);
    }

    @NonNull
    @Override
    public Observable<CharSequence> getQueryObservable() {
        return RxSearchable.create(searchView).map(RxSearchable.intoQuery());
    }

    @Override
    public void onTagSelected(long tagId, @NonNull String tagName) {
        Intent data = new Intent();
        data.putExtra(EXTRA_TAG_ID, tagId);
        data.putExtra(EXTRA_TAG_NAME, tagName);
        setResult(RESULT_OK, data);
        finish();
    }

    @NonNull
    @Override
    protected View getUndoRoot() {
        return root;
    }
}
