package com.github.st1hy.countthemcalories.ui.activities.ingredients.fragment.view;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.github.st1hy.countthemcalories.ui.activities.ingredients.view.IngredientsScreen;
import com.github.st1hy.countthemcalories.database.commands.UndoView;
import com.github.st1hy.countthemcalories.ui.core.dialog.DialogEvent;
import com.github.st1hy.countthemcalories.ui.core.state.Visibility;

import rx.Observable;

public interface IngredientsView extends UndoView, IngredientsScreen {

    void setNoIngredientsVisibility(@NonNull Visibility visibility);

    /**
     * @return observable that emits one item when action is confirmed by the user
     */
    @NonNull
    @CheckResult
    Observable<DialogEvent> showUsedIngredientRemoveConfirmationDialog();

    void scrollToPosition(int position);

    void setNoIngredientsMessage(@StringRes int noIngredientTextResId);

}
