package com.github.st1hy.countthemcalories.activities.ingredients.presenter;

import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.ingredients.model.IngredientTypesDatabaseModel;
import com.github.st1hy.countthemcalories.activities.ingredients.model.IngredientsModel;
import com.github.st1hy.countthemcalories.activities.ingredients.presenter.viewholder.EmptySpaceViewHolder;
import com.github.st1hy.countthemcalories.activities.ingredients.presenter.viewholder.IngredientItemViewHolder;
import com.github.st1hy.countthemcalories.activities.ingredients.presenter.viewholder.IngredientViewHolder;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsView;
import com.github.st1hy.countthemcalories.core.adapter.RxDaoRecyclerAdapter;
import com.github.st1hy.countthemcalories.core.rx.Functions;
import com.github.st1hy.countthemcalories.core.rx.RxPicasso;
import com.github.st1hy.countthemcalories.core.state.Visibility;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.parcel.IngredientTypeParcel;
import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensity;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import timber.log.Timber;

public class IngredientsDaoAdapter extends RxDaoRecyclerAdapter<IngredientViewHolder, IngredientTemplate>
        implements IngredientItemViewHolder.Callback {
    static final int bottomSpaceItem = 1;
    @LayoutRes
    static final int item_layout = R.layout.ingredients_item_scrolling;
    @LayoutRes
    static final int item_empty_space_layout = R.layout.ingredients_item_bottom_space;

    final IngredientsView view;
    final IngredientsModel model;
    final IngredientTypesDatabaseModel databaseModel;
    final Picasso picasso;

    @Inject
    public IngredientsDaoAdapter(@NonNull IngredientsView view,
                                 @NonNull IngredientsModel model,
                                 @NonNull IngredientTypesDatabaseModel databaseModel,
                                 @NonNull Picasso picasso) {
        super(databaseModel);
        this.view = view;
        this.model = model;
        this.databaseModel = databaseModel;
        this.picasso = picasso;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < getDaoItemCount()) {
            return item_layout;
        } else {
            return item_empty_space_layout;
        }
    }

    @Override
    public IngredientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        if (viewType == item_layout) {
            IngredientItemViewHolder holder = new IngredientItemViewHolder(view, this);
            holder.fillParent(parent);
            return holder;
        } else {
            return new EmptySpaceViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(IngredientViewHolder holder, int position) {
        if (holder instanceof IngredientItemViewHolder) {
            onBindToIngredientHolder((IngredientItemViewHolder) holder, position);
        }
    }

    private void onBindToIngredientHolder(@NonNull IngredientItemViewHolder holder, int position) {
        Cursor cursor = getCursor();
        if (cursor != null) {
            cursor.moveToPosition(position);
            IngredientTemplate ingredient = holder.getReusableIngredient();
            databaseModel.performReadEntity(cursor, ingredient);
            holder.setPosition(position);
            holder.setName(ingredient.getName());
            final EnergyDensity energyDensity = EnergyDensity.from(ingredient);
            holder.setEnergyDensity(model.getReadableEnergyDensity(energyDensity));
            onBindImage(ingredient, holder);
        } else {
            Timber.w("Cursor closed duding binding views.");
        }
    }

    void onBindImage(@NonNull IngredientTemplate ingredient, @NonNull IngredientItemViewHolder holder) {
        picasso.cancelRequest(holder.getImage());
        Uri imageUri = ingredient.getImageUri();
        if (imageUri != null && !imageUri.equals(Uri.EMPTY)) {
            RxPicasso.Builder.with(picasso, imageUri)
                    .centerCrop()
                    .fit()
                    .into(holder.getImage());
        } else {
            @DrawableRes int imageRes = ingredient.getAmountType() == AmountUnitType.VOLUME ?
                    R.drawable.ic_fizzy_drink : R.drawable.ic_fork_and_knife_wide;
            holder.getImage().setImageResource(imageRes);
        }
    }

    @Override
    protected void onCursorUpdate(@NonNull Cursor cursor) {
        super.onCursorUpdate(cursor);
        view.setNoIngredientButtonVisibility(Visibility.of(cursor.getCount() == 0));
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + bottomSpaceItem;
    }

    @Override
    public void onViewAttachedToWindow(IngredientViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (holder instanceof IngredientItemViewHolder) {
            IngredientItemViewHolder ingredientHolder = (IngredientItemViewHolder) holder;
            ingredientHolder.onAttached();
        }
    }

    @Override
    public void onViewDetachedFromWindow(IngredientViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (holder instanceof IngredientItemViewHolder) {
            IngredientItemViewHolder ingredientHolder = (IngredientItemViewHolder) holder;
            picasso.cancelRequest(ingredientHolder.getImage());
            ingredientHolder.onDetached();
        }
    }

    @Override
    public void onIngredientClicked(@NonNull IngredientTemplate ingredientTemplate, int position) {
        if (model.isInSelectMode()) {
            view.setResultAndReturn(new IngredientTypeParcel(ingredientTemplate));
        }
    }

    @Override
    public void onDeleteClicked(@NonNull IngredientTemplate ingredientTemplate, final int position) {
        databaseModel.getById(ingredientTemplate.getId())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(showConfirmationIfUsed())
                .flatMap(new Func1<IngredientTemplate, Observable<Cursor>>() {
                    @Override
                    public Observable<Cursor> call(IngredientTemplate ingredientTemplate) {
                        return databaseModel.removeAndRefresh(ingredientTemplate);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Cursor>() {
                    @Override
                    public void call(Cursor cursor) {
                        onCursorUpdate(cursor);
                        notifyItemRemoved(position);
                    }
                });
    }

    @NonNull
    private Func1<IngredientTemplate, Observable<IngredientTemplate>> showConfirmationIfUsed() {
        return new Func1<IngredientTemplate, Observable<IngredientTemplate>>() {
            @Override
            public Observable<IngredientTemplate> call(IngredientTemplate ingredientTemplate) {
                if (ingredientTemplate.getChildIngredients().isEmpty())
                    return Observable.just(ingredientTemplate);
                else
                    return view.showUsedIngredientRemoveConfirmationDialog()
                            .map(Functions.into(ingredientTemplate));
            }
        };
    }

    @Override
    public void onEditClicked(@NonNull IngredientTemplate ingredientTemplate, int position) {

    }
}
