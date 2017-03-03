package com.github.st1hy.countthemcalories.inject.activities.addingredient.fragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.AddIngredientFragment;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.AddIngredientModel;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.EnergyConverter;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.IngredientTagsModel;
import com.github.st1hy.countthemcalories.activities.addingredient.view.AddIngredientMenuAction;
import com.github.st1hy.countthemcalories.core.adapter.delegate.RecyclerViewAdapterDelegate;
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

@Module(includes = AddIngredientFragmentBindings.class)
public class AddIngredientFragmentModule {

    private final AddIngredientFragment fragment;
    private final Bundle savedState;

    public AddIngredientFragmentModule(AddIngredientFragment fragment, @Nullable Bundle savedState) {
        this.fragment = fragment;
        this.savedState = savedState;
    }

    @Provides
    @Nullable
    @Named("savedState")
    public Bundle provideSavedStateBundle() {
        return savedState;
    }

    @Provides
    @PerFragment
    public View rootView() {
        return fragment.getView();
    }


    @Provides
    @PerFragment
    public static IngredientTagsModel provideIngredientTagModel(@Nullable @Named("savedState") Bundle savedState,
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
    @PerFragment
    public static AddIngredientModel provideIngredientModel(
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
    public static RecyclerView recyclerView(
            View rootView,
            RecyclerViewAdapterDelegate adapter,
            RecyclerView.LayoutManager layoutManager,
            @Named("horizontalDivider") RecyclerView.ItemDecoration horizontalDivider,
            @Named("verticalDivider") RecyclerView.ItemDecoration verticalDivider) {

        RecyclerView tagsRecycler = (RecyclerView) rootView.findViewById(
                R.id.add_ingredient_categories_recycler);
        tagsRecycler.setNestedScrollingEnabled(true);
        tagsRecycler.setAdapter(adapter);
        tagsRecycler.setLayoutManager(layoutManager);
        tagsRecycler.addItemDecoration(horizontalDivider);
        tagsRecycler.addItemDecoration(verticalDivider);
        return tagsRecycler;
    }

    @Provides
    public RecyclerView.LayoutManager layoutManager(@Named("activityContext") Context context) {
        return ChipsLayoutManager.newBuilder(context).build();
    }

    @Provides
    @Named("horizontalDivider")
    public static RecyclerView.ItemDecoration horizontalDivider(@Named("activityContext") Context context,
                                                                @Named("divider") Drawable divider) {
        DividerItemDecoration itemDecoration = new DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL);
        itemDecoration.setDrawable(divider);
        return itemDecoration;
    }

    @Provides
    @Named("verticalDivider")
    public static RecyclerView.ItemDecoration verticalDivider(@Named("activityContext") Context context,
                                                              @Named("divider") Drawable divider) {
        DividerItemDecoration itemDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(divider);
        return itemDecoration;
    }

    @Provides
    public static Observable<AddIngredientMenuAction> menuActionObservable(PublishSubject<AddIngredientMenuAction> subject) {
        return subject.asObservable();
    }

    @Provides
    @Named("divider")
    @PerFragment
    public static Drawable invisibleDivider(@Named("activityContext") Context context) {
        return ContextCompat.getDrawable(context, R.drawable.invisible_divider);
    }

}