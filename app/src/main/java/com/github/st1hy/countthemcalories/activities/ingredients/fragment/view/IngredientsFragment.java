package com.github.st1hy.countthemcalories.activities.ingredients.fragment.view;

import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.AddIngredientType;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.inject.DaggerIngredientsFragmentComponent;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.inject.IngredientsFragmentComponent;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.inject.IngredientsFragmentModule;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.presenter.IngredientsDaoAdapter;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.presenter.IngredientsPresenter;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsScreen;
import com.github.st1hy.countthemcalories.core.baseview.BaseFragment;
import com.github.st1hy.countthemcalories.core.rx.RxAlertDialog;
import com.github.st1hy.countthemcalories.core.state.Visibility;
import com.github.st1hy.countthemcalories.core.tokensearch.SearchResult;
import com.github.st1hy.countthemcalories.database.parcel.IngredientTypeParcel;
import com.google.common.base.Preconditions;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

public class IngredientsFragment extends BaseFragment implements IngredientsView {
    public static final String ARG_SELECT_BOOL = "selection mode";

    IngredientsFragmentComponent component;

    @Inject
    IngredientsScreen screen;
    @Inject
    IngredientsPresenter presenter;
    @Inject
    IngredientsDaoAdapter adapter;

    @BindView(R.id.ingredients_empty)
    View emptyIngredients;
    @BindView(R.id.ingredients_empty_message)
    TextView emptyIngredientsText;
    @BindView(R.id.ingredients_content)
    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ingredients_content, container, false);
    }

    private IngredientsFragmentComponent getComponent() {
        if (component == null) {
            component = DaggerIngredientsFragmentComponent.builder()
                    .applicationComponent(getAppComponent())
                    .ingredientsFragmentModule(new IngredientsFragmentModule(this))
                    .build();
        }
        return component;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentActivity activity = getActivity();
        ButterKnife.bind(this, Preconditions.checkNotNull(getView()));
        getComponent().inject(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.onStart();
        adapter.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.onStart();
        adapter.onStop();
    }

    @Override
    public void setNoIngredientsVisibility(@NonNull Visibility visibility) {
        //noinspection WrongConstant
        emptyIngredients.setVisibility(visibility.getVisibility());
    }

    @NonNull
    @Override
    public Observable<Void> showUsedIngredientRemoveConfirmationDialog() {
        return RxAlertDialog.Builder.with(getActivity())
                .title(R.string.ingredients_remove_ingredient_dialog_title)
                .message(R.string.ingredients_remove_ingredient_dialog_message)
                .positiveButton(android.R.string.yes)
                .negativeButton(android.R.string.no)
                .show()
                .observePositiveClick();
    }

    public void onIngredientAdded(long addedIngredientId) {
        adapter.onIngredientAdded(addedIngredientId);
    }

    public void onSelectedNewIngredientType(@NonNull AddIngredientType type) {
        presenter.onSelectedNewIngredientType(type);
    }

    @Override
    public void scrollToPosition(int position) {
        recyclerView.scrollToPosition(position);
    }

    @Override
    public void setNoIngredientsMessage(@StringRes int noIngredientTextResId) {
        emptyIngredientsText.setText(noIngredientTextResId);
    }

    @Override
    @NonNull
    public Observable<SearchResult> getSearchObservable() {
        return screen.getSearchObservable();
    }

    @Override
    public void selectIngredientType() {
        screen.selectIngredientType();
    }

    @Override
    @NonNull
    public Observable<Void> getOnAddIngredientClickedObservable() {
        return screen.getOnAddIngredientClickedObservable();
    }

    @Override
    public void openNewIngredientScreen(@NonNull AddIngredientType type) {
        screen.openNewIngredientScreen(type);
    }

    @Override
    public void openEditIngredientScreen(long requestID, IngredientTypeParcel ingredientParcel) {
        screen.openEditIngredientScreen(requestID, ingredientParcel);
    }

    @Override
    public void onIngredientSelected(@NonNull IngredientTypeParcel ingredientTypeParcel) {
        screen.onIngredientSelected(ingredientTypeParcel);
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

    @Override
    public Observable<Integer> showAlertDialog(@StringRes int titleRes, @ArrayRes int optionsRes) {
        return screen.showAlertDialog(titleRes, optionsRes);
    }

    @Override
    public Observable<Integer> showAlertDialog(@StringRes int titleRes, CharSequence[] options) {
        return screen.showAlertDialog(titleRes, options);
    }

    @Override
    public void openNewMealScreen(@NonNull IngredientTypeParcel parcel) {
        screen.openNewMealScreen(parcel);
    }
}
