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
import com.github.st1hy.countthemcalories.core.adapter.callbacks.OnItemInteraction;
import com.github.st1hy.countthemcalories.core.rx.RxPicasso;
import com.github.st1hy.countthemcalories.core.state.Visibility;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.parcel.IngredientTypeParcel;
import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensity;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import timber.log.Timber;

public class IngredientsDaoAdapter extends RxDaoRecyclerAdapter<IngredientViewHolder, IngredientTemplate>
        implements OnItemInteraction<IngredientTemplate> {
    static final int bottomSpaceItem = 1;
    @LayoutRes
    static final int item_layout = R.layout.ingredients_item;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, null);
        if (viewType == item_layout) {
            return new IngredientItemViewHolder(view, this);
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
    public void onItemClicked(@NonNull IngredientTemplate ingredient) {
        if (model.isInSelectMode()) {
            view.setResultAndReturn(new IngredientTypeParcel(ingredient));
        }
    }

    @Override
    public void onItemLongClicked(@NonNull IngredientTemplate ingredient) {
        Timber.d("Long clicked on ingredient, %s", ingredient.getName());
    }


}
