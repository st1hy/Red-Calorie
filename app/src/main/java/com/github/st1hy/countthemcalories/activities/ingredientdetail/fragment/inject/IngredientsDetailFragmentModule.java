package com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.inject;

import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.presenter.IngredientDetailPresenter;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.presenter.IngredientDetailPresenterImpl;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.view.IngredientDetailFragment;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.fragment.view.IngredientDetailView;
import com.github.st1hy.countthemcalories.activities.ingredientdetail.view.IngredientDetailScreen;
import com.github.st1hy.countthemcalories.core.inject.PerFragment;
import com.github.st1hy.countthemcalories.core.permissions.PermissionsHelper;
import com.github.st1hy.countthemcalories.core.rx.RxPicasso;
import com.github.st1hy.countthemcalories.core.withpicture.imageholder.ImageHolderDelegate;
import com.google.common.base.Preconditions;
import com.squareup.picasso.Picasso;

import javax.inject.Named;
import javax.inject.Provider;

import dagger.Module;
import dagger.Provides;
import rx.Observable;

import static com.github.st1hy.countthemcalories.core.FragmentDepends.checkIsSubclass;

@Module
public class IngredientsDetailFragmentModule {

    public static final String EXTRA_INGREDIENT_TEMPLATE_PARCEL = "ingredient details extra template parcel";
    public static final String EXTRA_INGREDIENT_AMOUNT_BIGDECIMAL = "ingredient details extra amount";
    public static final String EXTRA_INGREDIENT_ID_LONG = "ingredient details extra id long";

    private final IngredientDetailFragment fragment;
    @Nullable
    private final Bundle savedState;

    public IngredientsDetailFragmentModule(@NonNull IngredientDetailFragment fragment,
                                           @Nullable Bundle savedState) {
        this.fragment = fragment;
        this.savedState = savedState;
    }

    @Provides
    @PerFragment
    public IngredientDetailPresenter providePresenter(IngredientDetailPresenterImpl presenter) {
        return presenter;
    }

    @Provides
    public IngredientDetailView provideView() {
        return fragment;
    }

    @Provides
    public Resources provideResources() {
        return fragment.getResources();
    }

    @Provides
    @Named("savedState")
    @Nullable
    public Bundle provideSavedState() {
        return savedState;
    }

    @Provides
    @Named("arguments")
    public Bundle provideArguments() {
        return Preconditions.checkNotNull(fragment.getArguments());
    }

    @Provides
    public IngredientDetailScreen provideScreen() {
        return checkIsSubclass(fragment.getActivity(), IngredientDetailScreen.class);
    }

    @Provides
    public FragmentActivity provideFragmentActivity() {
        return fragment.getActivity();
    }

    @Provides
    public ImageHolderDelegate provideImageHolderDelegate(Picasso picasso,
                                                          PermissionsHelper permissionsHelper,
                                                          Provider<ImageView> imageViewProvider) {
        return new ImageHolderDelegate(picasso, permissionsHelper, imageViewProvider) {
            @NonNull
            @Override
            protected Observable<RxPicasso.PicassoEvent> loadImage(@NonNull Uri uri) {
                return RxPicasso.Builder.with(picasso, uri)
                        .centerCrop()
                        .fit()
                        .into(imageViewProvider.get())
                        .asObservable();
            }
        };
    }

    @Provides
    public ImageView provideImageView(IngredientDetailView view) {
        return view.getImageView();
    }
}
