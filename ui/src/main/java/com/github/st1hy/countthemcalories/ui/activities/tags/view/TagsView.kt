package com.github.st1hy.countthemcalories.ui.activities.tags.view

import android.content.Context
import android.support.annotation.CheckResult
import android.support.annotation.StringRes
import android.view.View
import com.github.st1hy.countthemcalories.ui.R
import com.github.st1hy.countthemcalories.ui.core.dialog.CustomDialogViewEditTextController.editTextValueOnOk
import com.github.st1hy.countthemcalories.ui.core.dialog.DialogEvent
import com.github.st1hy.countthemcalories.ui.core.rx.RxAlertDialog
import com.github.st1hy.countthemcalories.ui.core.state.Visibility
import com.github.st1hy.countthemcalories.ui.inject.app.PerFragment
import com.github.st1hy.countthemcalories.ui.inject.quantifier.context.ActivityContext
import com.github.st1hy.countthemcalories.ui.inject.quantifier.view.FragmentRootView
import kotlinx.android.synthetic.main.tags_content.view.*
import rx.Observable
import javax.inject.Inject

internal interface TagsView : TagsScreen {
    /**
     * @return observable emitting new tag name to add
     */
    @CheckResult fun newTagDialog(@StringRes newTagDialogTitle: Int,
                                  initialText: String): Observable<String>

    fun setNoTagsVisibility(visibility: Visibility)

    /**
     * @return observable emitting onNext when user clicks ok to remove
     */
    @CheckResult fun showRemoveTagDialog(): Observable<DialogEvent>

    fun scrollToPosition(position: Int)

    fun setNoTagsMessage(@StringRes messageResId: Int)

}

@PerFragment internal class TagsViewImpl @Inject constructor(
        private val screen: TagsScreen,
        @FragmentRootView private val rootView: View,
        @ActivityContext private val context: Context
) : TagsScreen by screen, TagsView {

    override fun setNoTagsMessage(@StringRes messageResId: Int) {
        rootView.tags_empty_message.setText(messageResId)
    }

    override fun newTagDialog(@StringRes newTagDialogTitle: Int,
                              initialText: String): Observable<String> {
        val rxAlertDialog = RxAlertDialog.Builder.with(context)
                .title(newTagDialogTitle)
                .customView(R.layout.tags_new_tag_dialog_content)
                .positiveButton(android.R.string.ok)
                .negativeButton(android.R.string.cancel)
                .show()
        return editTextValueOnOk(rxAlertDialog, initialText, R.id.tags_dialog_name)
    }

    override fun setNoTagsVisibility(visibility: Visibility) {
        rootView.tags_empty.visibility = visibility.visibility
    }

    override fun showRemoveTagDialog() = RxAlertDialog.Builder.with(context)
            .title(R.string.tags_remove_dialog_title)
            .message(R.string.tags_remove_information)
            .positiveButton(android.R.string.yes)
            .negativeButton(android.R.string.no)
            .show()
            .basicEvents()

    override fun scrollToPosition(position: Int) {
        rootView.tags_recycler.scrollToPosition(position)
    }
}

