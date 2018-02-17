package com.github.st1hy.countthemcalories.ui.activities.ingredients.fragment.view

import android.content.Context
import android.support.annotation.CheckResult
import android.support.annotation.StringRes
import android.view.View
import com.github.st1hy.countthemcalories.ui.R
import com.github.st1hy.countthemcalories.ui.activities.ingredients.view.IngredientsScreen
import com.github.st1hy.countthemcalories.ui.core.command.undo.UndoView
import com.github.st1hy.countthemcalories.ui.core.dialog.DialogEvent
import com.github.st1hy.countthemcalories.ui.core.rx.RxAlertDialog
import com.github.st1hy.countthemcalories.ui.core.state.Visibility
import com.github.st1hy.countthemcalories.ui.inject.app.PerFragment
import com.github.st1hy.countthemcalories.ui.inject.quantifier.context.ActivityContext
import com.github.st1hy.countthemcalories.ui.inject.quantifier.view.FragmentRootView
import kotlinx.android.synthetic.main.ingredients_content.view.*
import rx.Observable
import javax.inject.Inject

internal interface IngredientsView : UndoView, IngredientsScreen {

    fun setNoIngredientsVisibility(visibility: Visibility)
    /**
     * @return observable that emits one item when action is confirmed by the user
     */
    @CheckResult
    fun showUsedIngredientRemoveConfirmationDialog(): Observable<DialogEvent>

    fun scrollToPosition(position: Int)
    fun setNoIngredientsMessage(@StringRes noIngredientTextResId: Int)
}

@PerFragment internal class IngredientsViewController @Inject constructor(
        undoView: UndoView,
        screen: IngredientsScreen,
        @ActivityContext private val context: Context,
        @FragmentRootView private val rootView: View
) : IngredientsView, UndoView by undoView, IngredientsScreen by screen {

    override fun setNoIngredientsVisibility(visibility: Visibility) {
        rootView.ingredients_empty.visibility = visibility.visibility
    }

    override fun showUsedIngredientRemoveConfirmationDialog(): Observable<DialogEvent> {
        return RxAlertDialog.Builder.with(context)
                .title(R.string.ingredients_remove_ingredient_dialog_title)
                .message(R.string.ingredients_remove_ingredient_dialog_message)
                .positiveButton(android.R.string.yes)
                .negativeButton(android.R.string.no)
                .show()
                .basicEvents()
    }

    override fun scrollToPosition(position: Int) {
        rootView.ingredients_content.scrollToPosition(position)
    }

    override fun setNoIngredientsMessage(@StringRes noIngredientTextResId: Int) {
        rootView.ingredients_empty_message.setText(noIngredientTextResId)
    }
}
