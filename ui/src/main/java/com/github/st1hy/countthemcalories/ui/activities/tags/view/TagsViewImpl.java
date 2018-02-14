package com.github.st1hy.countthemcalories.ui.activities.tags.view;

import android.content.Context;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.ui.R;
import com.github.st1hy.countthemcalories.ui.activities.tags.model.Tags;
import com.github.st1hy.countthemcalories.ui.core.baseview.Click;
import com.github.st1hy.countthemcalories.ui.core.dialog.DialogEvent;
import com.github.st1hy.countthemcalories.ui.core.rx.RxAlertDialog;
import com.github.st1hy.countthemcalories.ui.core.state.Visibility;
import com.github.st1hy.countthemcalories.ui.inject.app.PerFragment;
import com.github.st1hy.countthemcalories.ui.inject.quantifier.context.ActivityContext;
import com.github.st1hy.countthemcalories.ui.inject.quantifier.view.FragmentRootView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

import static com.github.st1hy.countthemcalories.ui.core.dialog.CustomDialogViewEditTextController.editTextValueOnOk;

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
                        @NonNull @FragmentRootView View rootView,
                        @NonNull @ActivityContext Context context) {
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
        return editTextValueOnOk(rxAlertDialog, initialText, R.id.tags_dialog_name);
    }

    @Override
    public void setNoTagsVisibility(@NonNull Visibility visibility) {
        //noinspection WrongConstant
        emptyTags.setVisibility(visibility.getVisibility());
    }

    @Override
    @NonNull
    public Observable<DialogEvent> showRemoveTagDialog() {
        return RxAlertDialog.Builder.with(context)
                .title(R.string.tags_remove_dialog_title)
                .message(R.string.tags_remove_information)
                .positiveButton(android.R.string.yes)
                .negativeButton(android.R.string.no)
                .show()
                .basicEvents();
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
    public void onTagsSelected(@NonNull Tags tag) {
        screen.onTagsSelected(tag);
    }

    @Override
    @NonNull
    public Observable<Click> addTagClickedObservable() {
        return screen.addTagClickedObservable();
    }

    @Override
    @NonNull
    public Observable<CharSequence> getQueryObservable() {
        return screen.getQueryObservable();
    }

    @Override
    public void setConfirmButtonVisibility(@NonNull Visibility visibility) {
        screen.setConfirmButtonVisibility(visibility);
    }

    @Override
    @NonNull
    @CheckResult
    public Observable<Click> confirmClickedObservable() {
        return screen.confirmClickedObservable();
    }
}
