package com.github.st1hy.countthemcalories.inject.activities.addingredient.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.AddIngredientFragment;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.AddIngredientModel;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.EnergyConverter;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.IngredientTagsModel;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.presenter.AddIngredientPresenter;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.presenter.AddIngredientPresenterImp;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.presenter.IngredientTagsPresenter;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.view.AddIngredientView;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.view.AddIngredientViewController;
import com.github.st1hy.countthemcalories.activities.addingredient.view.AddIngredientMenuAction;
import com.github.st1hy.countthemcalories.core.adapter.delegate.RecyclerAdapterWrapper;
import com.github.st1hy.countthemcalories.core.adapter.delegate.RecyclerViewAdapterDelegate;
import com.github.st1hy.countthemcalories.core.headerpicture.PictureModel;
import com.github.st1hy.countthemcalories.core.headerpicture.SelectPicturePresenter;
import com.github.st1hy.countthemcalories.core.headerpicture.SelectPicturePresenterImp;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.JointIngredientTag;
import com.github.st1hy.countthemcalories.database.Tag;
import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;
import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import org.joda.time.DateTime;
import org.parceler.Parcels;

import java.math.BigDecimal;
import java.util.ArrayList;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import rx.Observable;
import rx.subjects.PublishSubject;

@Module
public class AddIngredientFragmentModule {

    private final AddIngredientFragment fragment;
    private final Bundle savedState;

    public AddIngredientFragmentModule(AddIngredientFragment fragment, @Nullable Bundle savedState) {
        this.fragment = fragment;
        this.savedState = savedState;
    }

    @Provides
    public AddIngredientView provideView(AddIngredientViewController controller) {
        return controller;
    }

    @Provides
    public AddIngredientPresenter providePresenter(AddIngredientPresenterImp presenter) {
        return presenter;
    }

    @Provides
    @PerFragment
    public SelectPicturePresenter picturePresenter(SelectPicturePresenterImp presenter) {
        return presenter;
    }

    @Provides
    @PerFragment
    public IngredientTagsModel provideIngredientTagModel(@Nullable @Named("savedState") Bundle savedState,
                                                         @Nullable IngredientTemplate template) {
        if (savedState != null) {
            Parcelable parcelable = savedState.getParcelable(IngredientTagsModel.SAVED_TAGS_MODEL);
            return Parcels.unwrap(parcelable);
        } else {
            ArrayList<Tag> tags;
            if (template != null) {
                tags = Lists.newArrayList(Collections2.transform(template.getTags(),
                        JointIngredientTag::getTagOrNull));
            } else {
                tags = new ArrayList<>(5);
            }
            return new IngredientTagsModel(tags);
        }
    }

    @Provides
    @Nullable
    @Named("savedState")
    public Bundle provideSavedStateBundle() {
        return savedState;
    }


    @Provides
    @PerFragment
    public AddIngredientModel provideIngredientModel(
            @Nullable @Named("savedState") Bundle savedState,
            @Nullable IngredientTemplate templateSource,
            @NonNull @Named("initialName") String name,
            @NonNull AmountUnitType amountUnitType,
            @NonNull EnergyConverter energyConverter) {

        if (savedState != null) {
            Parcelable parcelable = savedState.getParcelable(
                    AddIngredientModel.SAVED_INGREDIENT_MODEL);
            return Parcels.unwrap(parcelable);
        } else {
            Long id;
            String energyValue;
            Uri imageUri;
            DateTime creationDate;
            if (templateSource != null) {
                id = templateSource.getId();
                name = templateSource.getName();
                amountUnitType = templateSource.getAmountType();
                final BigDecimal energyDensityAmount = templateSource.getEnergyDensityAmount();
                energyValue = energyConverter.fromDatabaseToCurrent(amountUnitType,
                        energyDensityAmount)
                        .toPlainString();
                imageUri = templateSource.getImageUri();
                creationDate = templateSource.getCreationDate();
            } else {
                id = null;
                energyValue = "";
                imageUri = Uri.EMPTY;
                creationDate = null;
            }
            return new AddIngredientModel(name, amountUnitType, energyValue, imageUri,
                    creationDate, id);
        }
    }

    @Provides
    @PerFragment
    public View rootView() {
        return fragment.getView();
    }

    @Provides
    @PerFragment
    public RecyclerView recyclerView(View rootView, RecyclerViewAdapterDelegate adapter) {
        RecyclerView tagsRecycler = (RecyclerView) rootView.findViewById(
                R.id.add_ingredient_categories_recycler);
        tagsRecycler.setAdapter(adapter);
        tagsRecycler.setLayoutManager(new LinearLayoutManager(fragment.getActivity()));
        tagsRecycler.setNestedScrollingEnabled(false);
        return tagsRecycler;
    }

    @Provides
    public RecyclerAdapterWrapper recyclerAdapterWrapper(IngredientTagsPresenter tagsPresenter) {
        return tagsPresenter;
    }

    @Provides
    public Observable<AddIngredientMenuAction> menuActionObservable(PublishSubject<AddIngredientMenuAction> subject) {
        return subject.asObservable();
    }

    @Provides
    public PictureModel pictureModel(AddIngredientModel model) {
        return model;
    }

}