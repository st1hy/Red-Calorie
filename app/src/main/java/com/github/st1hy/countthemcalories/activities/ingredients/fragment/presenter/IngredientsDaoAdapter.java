package com.github.st1hy.countthemcalories.activities.ingredients.fragment.presenter;

import android.database.Cursor;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.model.IngredientOptions;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.model.IngredientsFragmentModel;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.view.IngredientsView;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.viewholder.EmptySpaceViewHolder;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.viewholder.IngredientItemViewHolder;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.viewholder.IngredientViewHolder;
import com.github.st1hy.countthemcalories.activities.ingredients.model.RxIngredientsDatabaseModel;
import com.github.st1hy.countthemcalories.activities.ingredients.model.commands.IngredientsDatabaseCommands;
import com.github.st1hy.countthemcalories.core.adapter.CursorRecyclerViewAdapter;
import com.github.st1hy.countthemcalories.core.adapter.RecyclerEvent;
import com.github.st1hy.countthemcalories.core.command.undo.UndoTransformer;
import com.github.st1hy.countthemcalories.core.dialog.DialogView;
import com.github.st1hy.countthemcalories.core.headerpicture.imageholder.ImageHolderDelegate;
import com.github.st1hy.countthemcalories.core.permissions.PermissionsHelper;
import com.github.st1hy.countthemcalories.core.rx.Functions;
import com.github.st1hy.countthemcalories.core.rx.Schedulers;
import com.github.st1hy.countthemcalories.core.state.Visibility;
import com.github.st1hy.countthemcalories.core.tokensearch.LastSearchResult;
import com.github.st1hy.countthemcalories.core.tokensearch.SearchResult;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensity;
import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.Queue;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

import static com.github.st1hy.countthemcalories.activities.ingredients.fragment.model.IngredientOptions.from;

@PerFragment
public class IngredientsDaoAdapter extends CursorRecyclerViewAdapter<IngredientViewHolder>
        implements IngredientItemViewHolder.Callback {

    private static final int bottomSpaceItem = 1;
    @LayoutRes
    private static final int item_layout = R.layout.ingredients_item_scrolling;
    @LayoutRes
    private static final int item_empty_space_layout = R.layout.ingredients_item_bottom_space;

    @NonNull
    private final IngredientsView view;
    @NonNull
    private final IngredientsFragmentModel model;
    @NonNull
    private final RxIngredientsDatabaseModel databaseModel;
    @NonNull
    private final IngredientsDatabaseCommands commands;
    @NonNull
    private final Picasso picasso;
    @NonNull
    private final PermissionsHelper permissionsHelper;
    @NonNull
    private final LastSearchResult recentSearchResult;
    @NonNull
    private final Observable<SearchResult> searchResultObservable;
    @NonNull
    private final DialogView dialogView;

    private final Queue<Long> addedItems = new LinkedList<>();

    @Inject
    public IngredientsDaoAdapter(@NonNull IngredientsView view,
                                 @NonNull IngredientsFragmentModel model,
                                 @NonNull RxIngredientsDatabaseModel databaseModel,
                                 @NonNull IngredientsDatabaseCommands commands,
                                 @NonNull Picasso picasso,
                                 @NonNull PermissionsHelper permissionsHelper,
                                 @NonNull LastSearchResult recentSearchResult,
                                 @NonNull Observable<SearchResult> searchResultObservable,
                                 @NonNull DialogView dialogView) {
        this.view = view;
        this.model = model;
        this.databaseModel = databaseModel;
        this.commands = commands;
        this.picasso = picasso;
        this.permissionsHelper = permissionsHelper;
        this.recentSearchResult = recentSearchResult;
        this.searchResultObservable = searchResultObservable;
        this.dialogView = dialogView;
    }

    @Override
    public void onStart() {
        super.onStart();
        addSubscription(searchIngredients(searchResultObservable));
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
            IngredientItemViewHolder holder = new IngredientItemViewHolder(view, this, picasso, permissionsHelper);
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
            ingredientHolder.onDetached();
        }
    }

    @Override
    public void onIngredientClicked(@NonNull final IngredientTemplate ingredientTemplate,
                                    final int position) {
        if (model.isInSelectMode()) {
            view.onIngredientSelected(ingredientTemplate);
        } else {
            dialogView.showAlertDialog(model.getIngredientOptionsTitle(), model.getIngredientOptions())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(selectedOptionPosition -> {
                        IngredientOptions selectedOption = from(selectedOptionPosition);
                        switch (selectedOption) {
                            case ADD_TO_NEW:
                                view.addToNewMeal(ingredientTemplate);
                                break;
                            case EDIT:
                                onEditClicked(ingredientTemplate, position);
                                break;
                            case REMOVE:
                                onDeleteClicked(ingredientTemplate, position);
                                break;
                        }
                    });
        }
    }

    @Override
    public void onDeleteClicked(@NonNull IngredientTemplate ingredientTemplate, final int position) {
        databaseModel.getById(ingredientTemplate.getId())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(ingredientTemplate1 -> {
                    if (ingredientTemplate1.getChildIngredients().isEmpty())
                        return Observable.just(ingredientTemplate1);
                    else
                        return view.showUsedIngredientRemoveConfirmationDialog()
                                .map(Functions.into(ingredientTemplate1));
                })
                .flatMap(commands::delete)
                .doOnNext(deleteResponse -> addSubscription(deleteResponse.undoAvailability()
                        .compose(new UndoTransformer<>(deleteResponse,
                                isAvailable -> {
                                    if (isAvailable)
                                        return view.showUndoMessage(model.getUndoDeleteMessage());
                                    else {
                                        view.hideUndoMessage();
                                        return Observable.empty();
                                    }
                                }))
                        .subscribe(result -> {
                            int newItemPosition = result.getNewItemPositionInCursor();
                            onCursorUpdate(result.getCursor(), recentSearchResult.get());
                            if (newItemPosition != -1) {
                                notifyItemInsertedRx(newItemPosition);
                                view.scrollToPosition(newItemPosition);
                            } else {
                                notifyDataSetChanged();
                            }
                        })
                ))
                .map(Functions.intoResponse())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cursor -> {
                    onCursorUpdate(cursor, recentSearchResult.get());
                    notifyItemRemovedRx(position);
                });
    }

    @Override
    public void onEditClicked(@NonNull IngredientTemplate ingredientTemplate, final int position) {
        //Ingredient template here is not attached to database and is missing tags
        databaseModel.getByIdRecursive(ingredientTemplate.getId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ingredientTemplate1 -> {
                    view.editIngredientTemplate(position, ingredientTemplate1);
                });
    }

    @NonNull
    @Override
    public Observable<RecyclerEvent> getEvents() {
        return getEventSubject();
    }

    @NonNull
    private Subscription searchIngredients(@NonNull Observable<SearchResult> sequenceObservable) {
        return sequenceObservable
                .flatMap(searchResult1 -> {
                    Observable<Cursor> cursor1 = databaseModel.getAllFilteredBy(
                            searchResult1.getQuery(), searchResult1.getTokens()
                    );
                    return cursor1.map(cursor -> new QueryFinished(cursor, searchResult1));
                })
                .doOnError(e -> Timber.e(e, "Search exploded"))
                .retry(128)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(queryFinished -> {
                    SearchResult searchResult = queryFinished.getSearchingFor();
                    recentSearchResult.set(searchResult);
                    onCursorUpdate(queryFinished.getCursor(), searchResult);
                    notifyDataSetChanged();
                    onSearchFinished();
                });
    }


    private void onCursorUpdate(@NonNull Cursor cursor, @NonNull SearchResult searchingFor) {
        onCursorUpdate(cursor);
        boolean isSearchFilterEmpty = searchingFor.getQuery().trim().isEmpty() && searchingFor.getTokens().isEmpty();
        view.setNoIngredientsMessage(isSearchFilterEmpty ? model.getNoIngredientsMessage() : model.getSearchEmptyMessage());
        view.setNoIngredientsVisibility(Visibility.of(cursor.getCount() == 0));
    }

    public void onIngredientAdded(long addedIngredientId) {
        addedItems.offer(addedIngredientId);
    }

    private void onSearchFinished() {
        final Long newItemId = addedItems.poll();
        if (newItemId != null) {
            addSubscription(
                    Observable.fromCallable(
                            () -> databaseModel.findInCursor(
                                    getCursor(), newItemId))
                            .subscribeOn(Schedulers.computation())
                            .filter(IngredientsDaoAdapter::isSuccessful)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(view::scrollToPosition));
        }
    }

    private static boolean isSuccessful(int cursorPosition) {
        return cursorPosition != -1;
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
            Timber.w("Cursor closed during binding views.");
        }
    }

    private void onBindImage(@NonNull IngredientTemplate ingredient, @NonNull IngredientItemViewHolder holder) {
        holder.setImagePlaceholder(ingredient.getAmountType() == AmountUnitType.VOLUME ?
                R.drawable.ic_fizzy_drink : R.drawable.ic_fork_and_knife_wide);
        holder.setImageUri(ImageHolderDelegate.from(ingredient.getImageUri()));
    }

    private static class QueryFinished {
        final Cursor cursor;
        final SearchResult searchingFor;

        QueryFinished(@NonNull Cursor cursor, @NonNull SearchResult searchingFor) {
            this.cursor = cursor;
            this.searchingFor = searchingFor;
        }

        @NonNull
        public Cursor getCursor() {
            return cursor;
        }

        @NonNull
        SearchResult getSearchingFor() {
            return searchingFor;
        }
    }

}
