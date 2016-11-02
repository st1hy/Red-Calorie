package com.github.st1hy.countthemcalories.activities.addmeal.fragment.view;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.inject.AddMealFragmentComponent;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.inject.AddMealFragmentModule;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.inject.DaggerAddMealFragmentComponent;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.model.IngredientAction;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.presenter.AddMealPresenter;
import com.github.st1hy.countthemcalories.activities.addmeal.fragment.presenter.IngredientsAdapter;
import com.github.st1hy.countthemcalories.activities.addmeal.view.AddMealScreen;
import com.github.st1hy.countthemcalories.core.baseview.BaseFragment;
import com.github.st1hy.countthemcalories.core.state.Visibility;
import com.github.st1hy.countthemcalories.database.Ingredient;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

public class AddMealFragment extends BaseFragment implements AddMealView {

    AddMealFragmentComponent component;

    @Inject
    AddMealPresenter presenter;
    @Inject
    IngredientsAdapter adapter;
    @Inject
    AddMealScreen screen;

    @BindView(R.id.add_meal_name)
    EditText name;
    @BindView(R.id.add_meal_ingredients_list)
    RecyclerView ingredientList;
    @BindView(R.id.add_meal_empty_ingredients)
    View emptyIngredients;
    @BindView(R.id.add_meal_button_add_ingredient)
    Button addIngredientButton;
    @BindView(R.id.add_meal_total_calories)
    TextView totalCalories;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_meal_content, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ButterKnife.bind(this, Preconditions.checkNotNull(getView()));
        getComponent(savedInstanceState).inject(this);

        ingredientList.setAdapter(adapter);
        ingredientList.setLayoutManager(new LinearLayoutManager(getActivity()));
        ingredientList.setNestedScrollingEnabled(false);
    }

    @NonNull
    protected AddMealFragmentComponent getComponent(@Nullable Bundle savedState) {
        if (component == null) {
            component = DaggerAddMealFragmentComponent.builder()
                    .applicationComponent(getAppComponent())
                    .addMealFragmentModule(new AddMealFragmentModule(this, savedState))
                    .build();
        }
        return component;
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
        presenter.onStop();
        adapter.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        presenter.onSaveState(outState);
    }

    @Override
    public void setName(@NonNull String name) {
        this.name.setText(name);
    }

    @NonNull
    @Override
    public Observable<CharSequence> getNameObservable() {
        return RxTextView.textChanges(name);
    }

    @Override
    public void setEmptyIngredientsVisibility(@NonNull Visibility visibility) {
        //noinspection WrongConstant
        emptyIngredients.setVisibility(visibility.getVisibility());
    }

    @Override
    public void scrollTo(int itemPosition) {
        ingredientList.scrollToPosition(itemPosition);
    }

    @Override
    public void setTotalEnergy(@NonNull String totalEnergy) {
        totalCalories.setText(totalEnergy);
    }

    @Override
    public void showNameError(@NonNull Optional<String> error) {
        name.setError(error.orNull());
    }

    @NonNull
    @Override
    public Observable<Void> getAddIngredientObservable() {
        return screen.getAddIngredientObservable()
                .mergeWith(RxView.clicks(addIngredientButton));
    }

    @Override
    @NonNull
    public Observable<Void> getSaveClickedObservable() {
        return screen.getSaveClickedObservable();
    }

    @Override
    public void showSnackbarError(@NonNull Optional<String> ingredientsError) {
        screen.showSnackbarError(ingredientsError);
    }

    @Override
    public void onMealSaved() {
        screen.onMealSaved();
    }

    @Override
    public void openAddIngredient() {
        screen.openAddIngredient();
    }

    @Override
    public final void showIngredientDetails(long requestId,
                                            @NonNull Ingredient ingredient,
                                            @NonNull List<Pair<View, String>> sharedElements) {
        screen.showIngredientDetails(requestId, ingredient, sharedElements);
    }

    @Override
    public void openCameraAndGetPicture() {
        screen.openCameraAndGetPicture();
    }

    @Override
    public void pickImageFromGallery() {
        screen.pickImageFromGallery();
    }

    @Override
    @NonNull
    public Observable<Void> getSelectPictureObservable() {
        return screen.getSelectPictureObservable();
    }

    @Override
    @NonNull
    public Observable<Uri> getPictureSelectedObservable() {
        return screen.getPictureSelectedObservable();
    }

    @Override
    @NonNull
    public ImageView getImageView() {
        return screen.getImageView();
    }

    @Override
    public void showImageOverlay() {
        screen.showImageOverlay();
    }

    @Override
    public void hideImageOverlay() {
        screen.hideImageOverlay();
    }

    @Override
    public Observable<Integer> showAlertDialog(@StringRes int titleRes, @ArrayRes int optionsRes) {
        return screen.showAlertDialog(titleRes, optionsRes);
    }

    @Override
    public Observable<Integer> showAlertDialog(@StringRes int titleRes, CharSequence[] options) {
        return screen.showAlertDialog(titleRes, options);
    }

    @NonNull
    @Override
    public Observable<IngredientAction> getIngredientActionObservable() {
        return screen.getIngredientActionObservable();
    }
}