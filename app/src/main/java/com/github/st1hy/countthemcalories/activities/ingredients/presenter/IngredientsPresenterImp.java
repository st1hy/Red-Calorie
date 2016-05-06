package com.github.st1hy.countthemcalories.activities.ingredients.presenter;

import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.ingredients.model.IngredientTypesModel;
import com.github.st1hy.countthemcalories.activities.ingredients.model.IngredientsActivityModel;
import com.github.st1hy.countthemcalories.activities.ingredients.presenter.viewholder.EmptySpaceViewHolder;
import com.github.st1hy.countthemcalories.activities.ingredients.presenter.viewholder.IngredientItemViewHolder;
import com.github.st1hy.countthemcalories.activities.ingredients.presenter.viewholder.IngredientViewHolder;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsView;
import com.github.st1hy.countthemcalories.activities.settings.model.SettingsModel;
import com.github.st1hy.countthemcalories.core.callbacks.OnItemInteraction;
import com.github.st1hy.countthemcalories.core.presenter.RxDaoRecyclerAdapter;
import com.github.st1hy.countthemcalories.core.rx.RxPicasso;
import com.github.st1hy.countthemcalories.core.state.Visibility;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensity;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensityUnit;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import rx.Observable;
import rx.functions.Action1;
import timber.log.Timber;

import static com.github.st1hy.countthemcalories.core.state.Selection.SELECTED;

public class IngredientsPresenterImp extends RxDaoRecyclerAdapter<IngredientViewHolder, IngredientTemplate>
        implements IngredientsPresenter, OnItemInteraction<IngredientTemplate> {
    static final int bottomSpaceItem = 1;
    @LayoutRes
    static final int item_layout = R.layout.ingredients_item;
    @LayoutRes
    static final int item_empty_space_layout = R.layout.ingredients_item_bottom_space;

    final IngredientsView view;
    final IngredientsActivityModel activityModel;
    final IngredientTypesModel model;
    final SettingsModel settingsModel;
    final Picasso picasso;

    @Inject
    public IngredientsPresenterImp(@NonNull IngredientsView view,
                                   @NonNull IngredientsActivityModel activityModel,
                                   @NonNull IngredientTypesModel model,
                                   @NonNull SettingsModel settingsModel,
                                   @NonNull Picasso picasso) {
        super(model);
        this.view = view;
        this.activityModel = activityModel;
        this.model = model;
        this.settingsModel = settingsModel;
        this.picasso = picasso;
    }

    @Override
    public void onBackPressed() {
        if (view.isDrawerOpen()) {
            view.closeDrawer();
        } else {
            view.invokeActionBack();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        view.setMenuItemSelection(R.id.nav_ingredients, SELECTED);
        onAddIngredientClicked(view.getOnAddIngredientClickedObservable());
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onNavigationItemSelected(@IdRes int itemId) {
        if (itemId == R.id.nav_overview) {
            view.openOverviewScreen();
        } else if (itemId == R.id.nav_settings) {
            view.openSettingsScreen();
        } else if (itemId == R.id.nav_tags) {
            view.openTagsScreen();
        }
        view.setMenuItemSelection(itemId, SELECTED);
        view.closeDrawer();
    }

    @Override
    public boolean onClickedOnAction(@IdRes int actionItemId) {
        if (actionItemId == R.id.action_sorting) {
            return true;
        }
        return false;
    }

    void onAddIngredientClicked(@NonNull Observable<Void> observable) {
        subscribe(observable.subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                view.openNewIngredientScreen();
            }
        }));
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
            model.performReadEntity(cursor, ingredient);
            holder.setName(ingredient.getName());
            holder.setEnergyDensity(getReadableEnergyDensity(ingredient));
            onBindImage(ingredient, holder);
        } else {
            Timber.w("Cursor closed duding binding views.");
        }
    }

    String getReadableEnergyDensity(@NonNull IngredientTemplate ingredient) {
        EnergyDensity energyDensity = ingredient.getEnergyDensity();
        AmountUnitType amountUnitType = energyDensity.getAmountUnitType();
        EnergyDensityUnit preferredUnitOfType = settingsModel.getPreferedFrom(amountUnitType);
        EnergyDensity convertedToPreferred = energyDensity.convertTo(preferredUnitOfType);
        return settingsModel.getUnitPlural(convertedToPreferred);
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
            holder.getImage().setImageResource(R.drawable.ic_fork_and_knife_wide);
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

    }

    @Override
    public void onItemLongClicked(@NonNull IngredientTemplate ingredient) {
        Timber.d("Long clicked on ingredient, %s", ingredient.getName());
    }

}
