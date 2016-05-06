package com.github.st1hy.countthemcalories.activities.ingredients.presenter;

import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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
import com.github.st1hy.countthemcalories.core.rx.RxPicasso;
import com.github.st1hy.countthemcalories.core.state.Visibility;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensity;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensityUnit;
import com.squareup.picasso.Picasso;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static com.github.st1hy.countthemcalories.core.state.Selection.SELECTED;

public class IngredientsPresenterImp extends RecyclerView.Adapter<IngredientViewHolder> implements IngredientsPresenter, OnItemInteraction<IngredientTemplate> {
    public static int debounceTime = 250;
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

    final CompositeSubscription subscriptions = new CompositeSubscription();
    Observable<CharSequence> searchObservable;
    Cursor cursor;

    @Inject
    public IngredientsPresenterImp(@NonNull IngredientsView view,
                                   @NonNull IngredientsActivityModel activityModel,
                                   @NonNull IngredientTypesModel model,
                                   @NonNull SettingsModel settingsModel,
                                   @NonNull Picasso picasso) {
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
        view.setMenuItemSelection(R.id.nav_ingredients, SELECTED);
        if (searchObservable != null) onSearch(searchObservable);
        onAddIngredientClicked(view.getOnAddIngredientClickedObservable());
    }

    @Override
    public void onStop() {
        subscriptions.clear();
        closeCursor(true);
    }

    @Override
    public void onSearch(Observable<CharSequence> observable) {
        searchObservable = observable;
        Observable<CharSequence> sequenceObservable = observable
                .subscribeOn(AndroidSchedulers.mainThread());
        if (debounceTime > 0) {
            sequenceObservable = sequenceObservable.share();
            sequenceObservable = sequenceObservable
                    .limit(1)
                    .concatWith(
                            sequenceObservable
                                    .skip(1)
                                    .debounce(debounceTime, TimeUnit.MILLISECONDS)
                    );
        }
        Observable<Cursor> cursorObservable = sequenceObservable
                .doOnNext(new Action1<CharSequence>() {
                    @Override
                    public void call(CharSequence text) {
                        Timber.v("Search notification: queryText='%s'", text);
                    }
                })
                .flatMap(queryDatabaseFiltered())
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable e) {
                        Timber.e(e, "Search exploded");
                    }
                })
                .retry();
        subscribeToCursor(cursorObservable);
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
        subscriptions.add(observable.subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                view.openNewIngredientScreen();
            }
        }));
    }

    @Override
    public int getItemViewType(int position) {
        if (position < getIngredientsCount()) {
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
        Cursor cursor = this.cursor;
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
    public int getItemCount() {
        return getIngredientsCount() + bottomSpaceItem;
    }

    @Override
    public void onItemClicked(@NonNull IngredientTemplate ingredient) {

    }

    @Override
    public void onItemLongClicked(@NonNull IngredientTemplate ingredient) {
        Timber.d("Long clicked on ingredient, %s", ingredient.getName());
    }

    void subscribeToCursor(@NonNull Observable<Cursor> cursorObservable) {
        subscriptions.add(cursorObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Cursor>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "Error when providing cursor");
                    }

                    @Override
                    public void onNext(Cursor cursor) {
                        Timber.v("Db cursor query ended");
                        closeCursor(false);
                        IngredientsPresenterImp.this.cursor = cursor;
                        notifyDataSetChanged();
                        int newSize = cursor.getCount();
                        view.setNoIngredientButtonVisibility(Visibility.of(newSize == 0));
                    }
                }));
    }

    @NonNull
    Func1<? super CharSequence, ? extends Observable<Cursor>> queryDatabaseFiltered() {
        return new Func1<CharSequence, Observable<Cursor>>() {
            @Override
            public Observable<Cursor> call(CharSequence text) {
                return model.getAllFiltered(text.toString());
            }
        };
    }

    private void closeCursor(boolean notify) {
        Cursor cursor = this.cursor;
        this.cursor = null;
        if (cursor != null) {
            if (notify) notifyDataSetChanged();
            cursor.close();
        }
    }

    private int getIngredientsCount() {
        return cursor != null ? cursor.getCount() : 0;
    }
}
