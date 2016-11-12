package com.github.st1hy.countthemcalories.activities.ingredients.fragment.view;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsScreen;
import com.github.st1hy.countthemcalories.core.command.undo.UndoView;
import com.github.st1hy.countthemcalories.core.state.Visibility;

import rx.Observable;

public interface IngredientsView extends UndoView, IngredientsScreen {

    void setNoIngredientsVisibility(@NonNull Visibility visibility);

    /**
     * @return observable that emits one item when action is confirmed by the user
     */
    @NonNull
    Observable<Void> showUsedIngredientRemoveConfirmationDialog();

    void scrollToPosition(int position);

    void setNoIngredientsMessage(@StringRes int noIngredientTextResId);

}
