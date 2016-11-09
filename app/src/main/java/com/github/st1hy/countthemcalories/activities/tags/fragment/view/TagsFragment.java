package com.github.st1hy.countthemcalories.activities.tags.fragment.view;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.tags.fragment.inject.DaggerTagsFragmentComponent;
import com.github.st1hy.countthemcalories.activities.tags.fragment.inject.TagsFragmentComponent;
import com.github.st1hy.countthemcalories.activities.tags.fragment.inject.TagsFragmentModule;
import com.github.st1hy.countthemcalories.activities.tags.fragment.presenter.TagsDaoAdapter;
import com.github.st1hy.countthemcalories.activities.tags.view.TagsScreen;
import com.github.st1hy.countthemcalories.core.baseview.BaseFragment;
import com.github.st1hy.countthemcalories.core.rx.RxAlertDialog;
import com.github.st1hy.countthemcalories.core.state.Visibility;
import com.github.st1hy.countthemcalories.database.Tag;
import com.google.common.base.Preconditions;
import com.jakewharton.rxbinding.widget.RxTextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

public class TagsFragment extends BaseFragment implements TagsView {

    public static final String ARG_PICK_BOOL = "pick tag flag";
    public static final String ARG_EXCLUDED_TAGS_STRING_ARRAY = "excluded tags array";

    @Inject
    TagsDaoAdapter adapter;
    @Inject
    TagsScreen screen;

    @BindView(R.id.tags_recycler)
    RecyclerView recyclerView;
    @BindView(R.id.tags_empty)
    View emptyTags;
    @BindView(R.id.tags_empty_message)
    TextView emptyTagsMessage;

    TagsFragmentComponent component;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tags_content, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentActivity activity = getActivity();
        ButterKnife.bind(this, Preconditions.checkNotNull(getView()));
        getComponent().inject(this);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
    }

    @NonNull
    protected TagsFragmentComponent getComponent() {
        if (component == null) {
            component = DaggerTagsFragmentComponent.builder()
                    .applicationComponent(getAppComponent())
                    .tagsFragmentModule(new TagsFragmentModule(this))
                    .build();
        }
        return component;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.onStop();
    }

    @Override
    public void setNoTagsMessage(@StringRes int messageResId) {
        emptyTagsMessage.setText(messageResId);
    }

    @NonNull
    @Override
    public Observable<String> showEditTextDialog(@StringRes int newTagDialogTitle, @NonNull String initialText) {
        final RxAlertDialog rxAlertDialog = RxAlertDialog.Builder.with(getActivity())
                .title(newTagDialogTitle)
                .customView(R.layout.tags_new_tag_dialog_content)
                .positiveButton(android.R.string.ok)
                .negativeButton(android.R.string.cancel)
                .show();
        final EditText text = (EditText) Preconditions.checkNotNull(rxAlertDialog.getCustomView())
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
    public void setNoTagsVisibility(@NonNull Visibility visibility) {
        //noinspection WrongConstant
        emptyTags.setVisibility(visibility.getVisibility());
    }

    @Override
    @NonNull
    public Observable<Void> showRemoveTagDialog() {
        return RxAlertDialog.Builder.with(getActivity())
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

    @Override
    public void openIngredientsFilteredBy(@NonNull String tagName) {
        screen.openIngredientsFilteredBy(tagName);
    }

    @Override
    public void onTagSelected(@NonNull Tag tag) {
        screen.onTagSelected(tag);
    }

    @Override
    @NonNull
    public Observable<Void> getAddTagClickedObservable() {
        return screen.getAddTagClickedObservable();
    }

    @Override
    @NonNull
    public Observable<CharSequence> getQueryObservable() {
        return screen.getQueryObservable();
    }

    @Override
    @NonNull
    public Observable<Void> showUndoMessage(@StringRes int undoMessageResId) {
        return screen.showUndoMessage(undoMessageResId);
    }

    @Override
    public void hideUndoMessage() {
        screen.hideUndoMessage();
    }
}
