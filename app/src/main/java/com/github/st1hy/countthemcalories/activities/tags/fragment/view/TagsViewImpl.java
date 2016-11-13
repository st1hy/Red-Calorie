package com.github.st1hy.countthemcalories.activities.tags.fragment.view;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.tags.view.TagsScreen;
import com.github.st1hy.countthemcalories.core.inject.PerFragment;
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

@PerFragment
public class TagsViewImpl implements TagsView {

    @NonNull
    private final TagsScreen screen;
    @NonNull
    private final Context context;
    @NonNull
    private final RecyclerView recyclerView;

    @BindView(R.id.tags_empty)
    View emptyTags;
    @BindView(R.id.tags_empty_message)
    TextView emptyTagsMessage;

    @Inject
    public TagsViewImpl(@NonNull TagsScreen screen,
                        @NonNull View rootView,
                        @NonNull Context context,
                        @NonNull RecyclerView recyclerView) {
        this.screen = screen;
        this.context = context;
        this.recyclerView = recyclerView;
        ButterKnife.bind(this, rootView);
    }


    @Override
    public void setNoTagsMessage(@StringRes int messageResId) {
        emptyTagsMessage.setText(messageResId);
    }

    @NonNull
    @Override
    public Observable<String> showEditTextDialog(@StringRes int newTagDialogTitle, @NonNull String initialText) {
        final RxAlertDialog rxAlertDialog = RxAlertDialog.Builder.with(context)
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
        return RxAlertDialog.Builder.with(context)
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

}
