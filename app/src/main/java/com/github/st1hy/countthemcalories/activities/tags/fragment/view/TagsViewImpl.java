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
import com.github.st1hy.countthemcalories.core.rx.RxAlertDialog;
import com.github.st1hy.countthemcalories.core.state.Visibility;
import com.github.st1hy.countthemcalories.database.Tag;
import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.jakewharton.rxbinding.widget.RxTextView;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

import static com.google.common.base.Preconditions.checkNotNull;

@PerFragment
public class TagsViewImpl implements TagsView {

    @NonNull
    private final TagsScreen screen;
    @NonNull
    private final Context context;

    @BindView(R.id.tags_recycler)
    RecyclerView recyclerView;
    @BindView(R.id.tags_empty)
    View emptyTags;
    @BindView(R.id.tags_empty_message)
    TextView emptyTagsMessage;

    @Inject
    public TagsViewImpl(@NonNull TagsScreen screen,
                        @NonNull @Named("fragmentRootView") View rootView,
                        @NonNull @Named("activityContext") Context context) {
        this.screen = screen;
        this.context = context;
        ButterKnife.bind(this, rootView);
    }


    @Override
    public void setNoTagsMessage(@StringRes int messageResId) {
        emptyTagsMessage.setText(messageResId);
    }

    @NonNull
    @Override
    public Observable<String> newTagDialog(@StringRes int newTagDialogTitle,
                                           @NonNull String initialText) {
        final RxAlertDialog rxAlertDialog = RxAlertDialog.Builder.with(context)
                .title(newTagDialogTitle)
                .customView(R.layout.tags_new_tag_dialog_content)
                .positiveButton(android.R.string.ok)
                .negativeButton(android.R.string.cancel)
                .show();
        final EditText text = (EditText) checkNotNull(rxAlertDialog.getCustomView())
                .findViewById(R.id.tags_dialog_name);
        text.setText(initialText);
        text.setOnKeyListener((v, keyCode, event) -> {
            if (isEnterClicked(event)) {
                finishDialog(rxAlertDialog);
            }
            return false;
        });

        RxTextView.editorActions(text)
                .filter(actionId -> actionId == EditorInfo.IME_ACTION_DONE)
                .subscribe(actionId1 -> finishDialog(rxAlertDialog));
        return rxAlertDialog.observePositiveClick()
                .map(aVoid -> text.getText().toString());
    }

    private boolean finishDialog(@NonNull RxAlertDialog rxAlertDialog) {
        return rxAlertDialog.getDialog()
                .getButton(AlertDialog.BUTTON_POSITIVE)
                .performClick();
    }

    private static boolean isEnterClicked(KeyEvent event) {
        return event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_ENTER;
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
