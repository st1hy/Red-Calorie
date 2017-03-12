package com.github.st1hy.countthemcalories.activities.addingredient.fragment.inject;

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
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.adapter.IngredientTagsAdapter;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.AddIngredientModel;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.EnergyConverter;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.model.IngredientTagsModel;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.presenter.AddIngredientPresenter;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.presenter.AddIngredientPresenterImp;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.view.AddIngredientView;
import com.github.st1hy.countthemcalories.activities.addingredient.fragment.view.AddIngredientViewController;
import com.github.st1hy.countthemcalories.activities.addingredient.view.AddIngredientMenuAction;
import com.github.st1hy.countthemcalories.core.dialog.DialogModule;
import com.github.st1hy.countthemcalories.core.headerpicture.PictureModel;
import com.github.st1hy.countthemcalories.core.headerpicture.SelectPicturePresenter;
import com.github.st1hy.countthemcalories.core.headerpicture.SelectPicturePresenterImp;
import com.github.st1hy.countthemcalories.core.permissions.PermissionModule;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.JointIngredientTag;
import com.github.st1hy.countthemcalories.database.Tag;
import com.github.st1hy.countthemcalories.database.property.CreationSource;
import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;
import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.github.st1hy.countthemcalories.inject.quantifier.bundle.FragmentSavedState;
import com.github.st1hy.countthemcalories.inject.quantifier.context.ActivityContext;
import com.github.st1hy.countthemcalories.inject.quantifier.view.FragmentRootView;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import org.parceler.Parcels;

import java.math.BigDecimal;
import java.util.ArrayList;

import javax.inject.Named;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import rx.Observable;
import rx.subjects.PublishSubject;

@Module(includes = {
        PermissionModule.class,
        DialogModule.class,
        AddIngredientArgumentsModule.class
})
public abstract class AddIngredientFragmentModule {

    @Binds
    public abstract AddIngredientView provideView(AddIngredientViewController controller);

    @Binds
    public abstract AddIngredientPresenter providePresenter(AddIngredientPresenterImp presenter);

    @Binds
    @PerFragment
    public abstract SelectPicturePresenter picturePresenter(SelectPicturePresenterImp presenter);

    @Binds
    public abstract RecyclerView.Adapter recyclerAdapter(IngredientTagsAdapter tagsPresenter);

    @Binds
    public abstract PictureModel pictureModel(AddIngredientModel model);

    @Provides
    @PerFragment
    public static IngredientTagsModel provideIngredientTagModel(@Nullable @FragmentSavedState Bundle savedState,
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
            @Nullable @FragmentSavedState Bundle savedState,
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
            CreationSource creationSource;
            if (templateSource != null) {
                id = templateSource.getId();
                name = templateSource.getName();
                amountUnitType = templateSource.getAmountType();
                final BigDecimal energyDensityAmount = templateSource.getEnergyDensityAmount();
                energyValue = energyConverter.fromDatabaseToCurrent(amountUnitType,
                        energyDensityAmount)
                        .toPlainString();
                imageUri = templateSource.getImageUri();
                creationSource = templateSource.getCreationSource();
            } else {
                id = null;
                energyValue = "";
                imageUri = Uri.EMPTY;
                creationSource = CreationSource.USER;
            }
            return new AddIngredientModel(name, amountUnitType, energyValue, imageUri,
                    creationSource, id);
        }
    }

    @Provides
    @PerFragment
    public static RecyclerView recyclerView(
            @FragmentRootView View rootView,
            RecyclerView.Adapter adapter,
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
    public static RecyclerView.LayoutManager layoutManager(@ActivityContext Context context) {
        return ChipsLayoutManager.newBuilder(context).build();
    }

    @Provides
    @Named("horizontalDivider")
    public static RecyclerView.ItemDecoration horizontalDivider(@ActivityContext Context context,
                                                                @Named("divider") Drawable divider) {
        DividerItemDecoration itemDecoration = new DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL);
        itemDecoration.setDrawable(divider);
        return itemDecoration;
    }

    @Provides
    @Named("verticalDivider")
    public static RecyclerView.ItemDecoration verticalDivider(@ActivityContext Context context,
                                                              @Named("divider") Drawable divider) {
        DividerItemDecoration itemDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(divider);
        return itemDecoration;
    }

    @Provides
    public static Observable<AddIngredientMenuAction> menuActionObservable(
            PublishSubject<AddIngredientMenuAction> subject) {
        return subject.asObservable();
    }

    @Provides
    @Named("divider")
    @PerFragment
    public static Drawable invisibleDivider(@ActivityContext Context context) {
        return ContextCompat.getDrawable(context, R.drawable.invisible_divider);
    }

}