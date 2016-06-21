package com.github.st1hy.countthemcalories.activities.tags.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsActivity;
import com.github.st1hy.countthemcalories.activities.tags.inject.DaggerTagsComponent;
import com.github.st1hy.countthemcalories.activities.tags.inject.TagsComponent;
import com.github.st1hy.countthemcalories.activities.tags.inject.TagsModule;
import com.github.st1hy.countthemcalories.activities.tags.presenter.TagsDaoAdapter;
import com.github.st1hy.countthemcalories.activities.tags.presenter.TagsPresenter;
import com.github.st1hy.countthemcalories.core.command.view.UndoDrawerActivity;
import com.github.st1hy.countthemcalories.core.rx.RxAlertDialog;
import com.github.st1hy.countthemcalories.core.state.Visibility;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxSearchView;
import com.jakewharton.rxbinding.widget.RxTextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

public class TagsActivity extends UndoDrawerActivity implements TagsView {
    public static final String ACTION_PICK_TAG = "pick tag";
    public static final String EXTRA_EXCLUDE_TAG_STRING_ARRAY = "exclude tag id";
    public static final String EXTRA_TAG_ID = "extra tag id";
    public static final String EXTRA_TAG_NAME = "extra tag name";

    protected TagsComponent component;

    @Inject
    TagsPresenter presenter;
    @Inject
    TagsDaoAdapter adapter;

    @BindView(R.id.tags_recycler)
    RecyclerView recyclerView;
    @BindView(R.id.tags_add_new)
    FloatingActionButton fab;
    @BindView(R.id.tags_empty)
    View emptyTags;
    @BindView(R.id.tags_empty_message)
    TextView emptyTagsMessage;
    @BindView(R.id.tags_root)
    CoordinatorLayout root;

    SearchView searchView;

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
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        onBind();
    }

    @NonNull
    @Override
    public Observable<Void> getOnAddTagClickedObservable() {
        return RxView.clicks(fab);
    }

    @Override
    public void setNoTagsMessage(@StringRes int messageResId) {
        emptyTagsMessage.setText(messageResId);
    }

    @Override
    public void openIngredientsFilteredBy(@NonNull String tagName) {
        Intent intent = new Intent(this, IngredientsActivity.class);
        intent.putExtra(IngredientsActivity.EXTRA_TAG_FILTER_STRING, tagName);
        startActivity(intent);
    }

    @NonNull
    @Override
    public Observable<String> showEditTextDialog(@StringRes int newTagDialogTitle, @NonNull String initialText) {
        final RxAlertDialog rxAlertDialog = RxAlertDialog.Builder.with(this)
                .title(newTagDialogTitle)
                .customView(R.layout.tags_new_tag_dialog_content)
                .positiveButton(android.R.string.ok)
                .negativeButton(android.R.string.cancel)
                .show();
        final EditText text = (EditText) assertNotNull(rxAlertDialog.getCustomView())
                .findViewById(R.id.tags_dialog_name);
        text.setText(initialText);
        text.setOnKeyListener(closeOnEnter(rxAlertDialog));
        RxTextView.editorActions(text)
                .filter(imeActionDone())
                .subscribe(closeDialog(rxAlertDialog));
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
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE); //Fills toolbar
        adapter.onSearch(RxSearchView.queryTextChanges(searchView));
        return true;
    }

    @Override
    public void setNoTagsVisibility(@NonNull Visibility visibility) {
        //noinspection WrongConstant
        emptyTags.setVisibility(visibility.getVisibility());
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
    public void setResultAndReturn(long tagId, @NonNull String tagName) {
        Intent data = new Intent();
        data.putExtra(EXTRA_TAG_ID, tagId);
        data.putExtra(EXTRA_TAG_NAME, tagName);
        setResult(RESULT_OK, data);
        finish();
    }

    @NonNull
    private View.OnKeyListener closeOnEnter(final RxAlertDialog rxAlertDialog) {
        return new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    closeDialog(rxAlertDialog).call(0);
                }
                return false;
            }
        };
    }

    @NonNull
    private Func1<Integer, Boolean> imeActionDone() {
        return new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer actionId) {
                return actionId == EditorInfo.IME_ACTION_DONE;
            }
        };
    }

    @NonNull
    private Action1<Integer> closeDialog(final RxAlertDialog rxAlertDialog) {
        return new Action1<Integer>() {
            @Override
            public void call(Integer actionId) {
                rxAlertDialog.getDialog().getButton(AlertDialog.BUTTON_POSITIVE).performClick();
            }
        };
    }

    @NonNull
    @Override
    protected View getUndoRoot() {
        return root;
    }
}
