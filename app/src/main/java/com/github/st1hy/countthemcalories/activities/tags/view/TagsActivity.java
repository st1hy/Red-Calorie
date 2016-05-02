package com.github.st1hy.countthemcalories.activities.tags.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.tags.inject.DaggerTagsComponent;
import com.github.st1hy.countthemcalories.activities.tags.inject.TagsComponent;
import com.github.st1hy.countthemcalories.activities.tags.inject.TagsModule;
import com.github.st1hy.countthemcalories.activities.tags.presenter.TagsPresenter;
import com.github.st1hy.countthemcalories.core.rx.RxAlertDialog;
import com.github.st1hy.countthemcalories.core.ui.BaseActivity;
import com.github.st1hy.countthemcalories.core.ui.Visibility;
import com.jakewharton.rxbinding.support.v4.widget.RxSwipeRefreshLayout;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxSearchView;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.functions.Func1;

public class TagsActivity extends BaseActivity implements TagsView {
    public static final String ACTION_PICK_TAG = "pick tag";
    public static final String EXTRA_TAG_ID = "extra tag id";

    TagsComponent component;

    @Inject
    TagsPresenter presenter;
    @Inject
    RecyclerView.Adapter adapter;

    @Bind(R.id.tags_toolbar)
    Toolbar toolbar;
    @Bind(R.id.tags_recycler)
    RecyclerView recyclerView;
    @Bind(R.id.tags_add_new)
    FloatingActionButton fab;
    @Bind(R.id.tags_no_tags_button)
    View notTagsButton;
    @Bind(R.id.tags_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    @NonNull
    protected TagsComponent getComponent() {
        if (component == null) {
            component = DaggerTagsComponent.builder()
                    .applicationComponent(getAppComponent())
                    .tagsModule(new TagsModule(this))
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
        setSupportActionBar(toolbar);
        assertNotNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        presenter.onAddTagClicked(Observable.merge(RxView.clicks(fab), RxView.clicks(notTagsButton)));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        presenter.onRefresh(RxSwipeRefreshLayout.refreshes(swipeRefreshLayout));
        swipeRefreshLayout.setEnabled(false);
    }

    @NonNull
    @Override
    public Observable<String> showEditTextDialog(int newTagDialogTitle) {
        final RxAlertDialog rxAlertDialog = RxAlertDialog.Builder.with(this)
                .title(newTagDialogTitle)
                .customView(R.layout.tags_new_tag_dialog_content)
                .positiveButton(android.R.string.ok)
                .show();
        final EditText text = (EditText) assertNotNull(rxAlertDialog.getCustomView())
                .findViewById(R.id.tags_dialog_name);
        return rxAlertDialog.observePositiveClick()
                .map(new Func1<Void, String>() {
                    @Override
                    public String call(Void aVoid) {
                        return text.getText().toString();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.tags_menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        presenter.onSearch(RxSearchView.queryTextChanges(searchView));
        return true;
    }

    @Override
    public void setNoTagsButtonVisibility(@NonNull Visibility visibility) {
        //noinspection WrongConstant
        notTagsButton.setVisibility(visibility.getVisibility());
    }

    @Override
    public void setDataRefreshing(boolean isRefreshing) {
        swipeRefreshLayout.setRefreshing(isRefreshing);
    }

    @Override
    @NonNull
    public Observable<Void> showRemoveTagDialog() {
        return RxAlertDialog.Builder.with(this)
                .title(R.string.tags_remove_dialog_title)
                .message(R.string.tags_remove_information)
                .positiveButton(android.R.string.yes)
                .negativeButton(android.R.string.no)
                .show()
                .observePositiveClick();
    }

    @Override
    public void scrollToPosition(int position) {
        recyclerView.scrollToPosition(position);
    }

    @Override
    public void setResultAndReturn(long tagId) {
        Intent data = new Intent();
        data.putExtra(EXTRA_TAG_ID, tagId);
        setResult(RESULT_OK, data);
        finish();
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
}
