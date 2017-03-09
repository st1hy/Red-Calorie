package com.github.st1hy.countthemcalories.activities.ingredients.fragment.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsScreen;
import com.github.st1hy.countthemcalories.core.command.undo.UndoView;
import com.github.st1hy.countthemcalories.core.dialog.DialogEvent;
import com.github.st1hy.countthemcalories.core.rx.RxAlertDialog;
import com.github.st1hy.countthemcalories.core.state.Visibility;
import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.github.st1hy.countthemcalories.inject.quantifier.context.ActivityContext;
import com.github.st1hy.countthemcalories.inject.quantifier.view.FragmentRootView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

import static android.R.string.no;

@PerFragment
public class IngredientsViewController extends IngredientsViewDelegate {

    @NonNull
    private final Context context;

    @BindView(R.id.ingredients_content)
    RecyclerView recyclerView;
    @BindView(R.id.ingredients_empty)
    View emptyIngredients;
    @BindView(R.id.ingredients_empty_message)
    TextView emptyIngredientsText;

    @Inject
    public IngredientsViewController(@NonNull UndoView undoView,
                                     @NonNull IngredientsScreen screen,
                                     @NonNull @ActivityContext Context context,
                                     @NonNull @FragmentRootView View rootView) {
        super(undoView, screen);
        this.context = context;
        ButterKnife.bind(this, rootView);
    }

    @Override
    public void setNoIngredientsVisibility(@NonNull Visibility visibility) {
        //noinspection WrongConstant
        emptyIngredients.setVisibility(visibility.getVisibility());
    }

    @NonNull
    @Override
    public Observable<DialogEvent> showUsedIngredientRemoveConfirmationDialog() {
        return RxAlertDialog.Builder.with(context)
                .title(R.string.ingredients_remove_ingredient_dialog_title)
                .message(R.string.ingredients_remove_ingredient_dialog_message)
                .positiveButton(android.R.string.yes)
                .negativeButton(no)
                .show()
                .basicEvents();
    }

    @Override
    public void scrollToPosition(int position) {
        recyclerView.scrollToPosition(position);
    }

    @Override
    public void setNoIngredientsMessage(@StringRes int noIngredientTextResId) {
        emptyIngredientsText.setText(noIngredientTextResId);
    }
}
