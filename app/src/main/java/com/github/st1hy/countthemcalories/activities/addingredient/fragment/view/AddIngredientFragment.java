package com.github.st1hy.countthemcalories.activities.addingredient.fragment.view;

import android.content.Intent;
import android.net.Uri;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.inject.AddIngredientFragmentComponent;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.inject.AddIngredientFragmentModule;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.inject.DaggerAddIngredientFragmentComponent;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.presenter.AddIngredientPresenter;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.presenter.IngredientTagsAdapter;
import com.github.st1hy.countthemcalories.activities.addingredient.view.AddIngredientScreen;
import com.github.st1hy.countthemcalories.core.baseview.BaseFragment;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.Collection;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

public class AddIngredientFragment extends BaseFragment implements AddIngredientView {

    AddIngredientFragmentComponent component;

    @Inject
    AddIngredientPresenter presenter;
    @Inject
    IngredientTagsAdapter tagsPresenter;
    @Inject
    AddIngredientScreen screen;

    @BindView(R.id.add_ingredient_name)
    EditText name;
    @BindView(R.id.add_ingredient_energy_density)
    EditText energyDensityValue;
    @BindView(R.id.add_ingredient_unit)
    Button energyDensityUnit;
    @BindView(R.id.add_ingredient_categories_recycler)
    RecyclerView tagsRecycler;
    @BindView(R.id.add_ingredient_name_search)
    View searchName;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_ingredient_content, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentActivity activity = getActivity();
        ButterKnife.bind(this, Preconditions.checkNotNull(getView()));
        getComponent(savedInstanceState).inject(this);

        tagsRecycler.setAdapter(tagsPresenter);
        tagsRecycler.setLayoutManager(new LinearLayoutManager(activity));
        tagsRecycler.setNestedScrollingEnabled(false);
    }

    @NonNull
    protected AddIngredientFragmentComponent getComponent(@Nullable Bundle savedState) {
        if (component == null) {
            component = DaggerAddIngredientFragmentComponent.builder()
                    .applicationComponent(getAppComponent())
                    .addIngredientFragmentModule(new AddIngredientFragmentModule(this, savedState))
                    .build();
        }
        return component;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        presenter.onSaveState(outState);
        tagsPresenter.onSaveState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.onStop();
    }

    @Override
    public void setSelectedUnitName(@NonNull String unitName) {
        energyDensityUnit.setText(unitName);
    }

    @Override
    public void setName(@NonNull String name) {
        this.name.setText(name);
    }

    @Override
    public void setEnergyDensityValue(@NonNull String energyValue) {
        this.energyDensityValue.setText(energyValue);
    }

    @NonNull
    @Override
    public Observable<CharSequence> getNameObservable() {
        return RxTextView.textChanges(name).skip(1);
    }

    @NonNull
    @Override
    public Observable<CharSequence> getValueObservable() {
        return RxTextView.textChanges(energyDensityValue).skip(1);
    }

    @Override
    public void showNameError(@NonNull Optional<Integer> errorResId) {
        if (errorResId.isPresent()) {
            name.setError(getString(errorResId.get()));
        } else {
            name.setError(null);
        }
    }

    @Override
    public void showValueError(@NonNull Optional<Integer> errorResId) {
        if (errorResId.isPresent()) {
            energyDensityValue.setError(getString(errorResId.get()));
        } else {
            energyDensityValue.setError(null);
        }
    }

    @Override
    public void requestFocusToName() {
        name.requestFocus();
    }

    @Override
    public void requestFocusToValue() {
        energyDensityValue.requestFocus();
    }

    public void onNewTagAddedToIngredient(long tagId, @NonNull String tagName) {
        tagsPresenter.onNewTagAdded(tagId, tagName);
    }

    @Override
    public void setResultAndFinish(@NonNull Intent intent) {
        screen.setResultAndFinish(intent);
    }

    @Override
    public void openSelectTagScreen(@NonNull Collection<String> tagNames) {
        screen.openSelectTagScreen(tagNames);
    }

    @Override
    @NonNull
    public Observable<Void> getSaveObservable() {
        return screen.getSaveObservable();
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

    @Override
    public void showInWebBrowser(@NonNull Uri address) {
        screen.showInWebBrowser(address);
    }

    @NonNull
    @Override
    public Observable<Void> getSearchObservable() {
        return RxView.clicks(searchName);
    }

    @NonNull
    @Override
    public Observable<Void> getSelectTypeObservable() {
        return RxView.clicks(energyDensityUnit);
    }
}
