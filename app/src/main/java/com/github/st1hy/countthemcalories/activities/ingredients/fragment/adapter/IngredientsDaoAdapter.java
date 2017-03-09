package com.github.st1hy.countthemcalories.activities.ingredients.fragment.adapter;

import android.database.Cursor;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.adapter.inject.IngredientViewHolderComponent;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.adapter.inject.IngredientViewHolderModule;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.adapter.viewholder.AbstractIngredientsViewHolder;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.adapter.viewholder.IngredientViewHolder;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.inject.IngredientsFragmentComponent;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.model.IngredientsFragmentModel;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.view.IngredientsView;
import com.github.st1hy.countthemcalories.activities.ingredients.model.RxIngredientsDatabaseModel;
import com.github.st1hy.countthemcalories.core.adapter.CursorRecyclerViewAdapter;
import com.github.st1hy.countthemcalories.core.command.InsertResult;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.IngredientTemplateDao;
import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensity;
import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.l4digital.fastscroll.FastScroller;

import javax.inject.Inject;

import timber.log.Timber;

@PerFragment
public class IngredientsDaoAdapter extends CursorRecyclerViewAdapter<AbstractIngredientsViewHolder>
        implements FastScroller.SectionIndexer {

    @LayoutRes
    private static final int ingredient_item_layout = R.layout.ingredients_item_scrolling;
    @LayoutRes
    private static final int space_layout = R.layout.list_item_bottom;
    private static final int space_layout_top = R.layout.list_item_top;
    private static final int SPACE_TOP = 1;
    private static final int SPACE_BOTTOM = 1;
    private static final int SPACE = 2;

    @Inject
    IngredientsFragmentModel model;
    @Inject
    IngredientsFragmentComponent component;
    @Inject
    RxIngredientsDatabaseModel databaseModel;
    @Inject
    IngredientsView view;


    @Inject
    public IngredientsDaoAdapter() {
    }

    public int findPositionById(long itemId) {
        return databaseModel.findInCursor(getCursor(), itemId);
    }

    public void onIngredientInserted(@NonNull InsertResult result) {
        int newItemPosition = result.getNewItemPositionInCursor();
        int previousCursorSize = getDaoItemCount();
        onCursorUpdate(result.getCursor());
        if (newItemPosition != -1) {
            newItemPosition = positionInAdapter(newItemPosition);
            notifyListenersItemInserted(newItemPosition);
            if (previousCursorSize > 0) {
                notifyItemInserted(newItemPosition);
            } else {
                notifyDataSetChanged();
            }
            view.scrollToPosition(newItemPosition);
        } else {
            notifyDataSetChanged();
        }
    }

    public void onIngredientDeleted(@NonNull Cursor cursor, int deletedPositionInAdapter) {
        int previousCursorSize = getDaoItemCount();
        onCursorUpdate(cursor);
        if (cursor.getCount() > 0) {
            notifyListenersItemRemove(deletedPositionInAdapter);
            if (previousCursorSize > 1) {
                notifyItemRemoved(deletedPositionInAdapter);
            } else {
                notifyDataSetChanged();
            }
        } else {
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position < SPACE_TOP) {
            return space_layout_top;
        } else if (position < getDaoItemCount() + SPACE_BOTTOM) {
            return ingredient_item_layout;
        } else {
            return space_layout;
        }
    }

    @Override
    public int getItemCount() {
        int itemCount = super.getItemCount();
        return itemCount == 0 ? 0 : itemCount + SPACE;
    }

    @Override
    public AbstractIngredientsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        IngredientViewHolderComponent holderComponent = component
                .newIngredientViewHolderComponent(new IngredientViewHolderModule(view));
        if (viewType == ingredient_item_layout) {
            IngredientViewHolder holder = holderComponent.newIngredientViewHolder();
            holder.fillParent(parent);
            return holder;
        } else {
            return holderComponent.newSpaceViewHolder();
        }
    }

    @Override
    public void onBindViewHolder(AbstractIngredientsViewHolder holder, int position) {
        if (holder instanceof IngredientViewHolder) {
            onBindViewHolder((IngredientViewHolder) holder, position);
        }
    }

    @Override
    public void onViewAttachedToWindow(AbstractIngredientsViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.onAttached();
    }

    @Override
    public void onViewDetachedFromWindow(AbstractIngredientsViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.onDetached();
    }

    public void onCursorUpdate(@NonNull Cursor cursor) {
        super.onCursorUpdate(cursor);
    }

    private void onBindViewHolder(IngredientViewHolder holder, int position) {
        Cursor cursor = getCursor();
        if (cursor != null) {
            cursor.moveToPosition(positionInCursor(position));
            IngredientTemplate ingredient = holder.getReusableIngredient();
            holder.setEnabled(true);
            databaseModel.performReadEntity(cursor, ingredient);
            holder.setPosition(position);
            holder.setName(ingredient.getName());
            final EnergyDensity energyDensity = EnergyDensity.from(ingredient);
            holder.setEnergyDensity(model.getReadableEnergyDensity(energyDensity));
            holder.setUnit(model.getUnit(energyDensity));
            holder.setImagePlaceholder(ingredient.getAmountType() == AmountUnitType.VOLUME ?
                    R.drawable.ic_fizzy_drink : R.drawable.ic_fork_and_knife_wide);
            holder.setImageUri(ingredient.getImageUri());
        } else {
            Timber.w("Cursor closed during binding views.");
        }
    }

    @Override
    public String getSectionText(int position) {
        position = positionInCursor(position);
        if (position >= getDaoItemCount()) {
            position = getDaoItemCount() - 1;
        } else if (position < 0) {
            position = 0;
        }
        Cursor cursor = getCursor();
        if (cursor != null) {
            cursor.moveToPosition(position);
            int index = cursor.getColumnIndexOrThrow(IngredientTemplateDao.Properties.Name.columnName);
            return cursor.getString(index).substring(0, 1);
        } else {
            return "";
        }
    }

    public static int positionInCursor(int positionInAdapter) {
        return positionInAdapter - SPACE_TOP;
    }

    public static int positionInAdapter(int positionInCursor) {
        return positionInCursor + SPACE_TOP;
    }

}
