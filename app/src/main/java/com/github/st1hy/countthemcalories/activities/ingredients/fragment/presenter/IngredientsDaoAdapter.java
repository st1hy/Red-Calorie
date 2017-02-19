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
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.viewholder.AbstractIngredientsViewHolder;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.viewholder.IngredientViewHolder;
import com.github.st1hy.countthemcalories.activities.ingredients.model.RxIngredientsDatabaseModel;
import com.github.st1hy.countthemcalories.activities.ingredients.model.commands.IngredientsDatabaseCommands;
import com.github.st1hy.countthemcalories.core.adapter.CursorRecyclerViewAdapter;
import com.github.st1hy.countthemcalories.core.adapter.RecyclerEvent;
import com.github.st1hy.countthemcalories.core.command.undo.UndoTransformer;
import com.github.st1hy.countthemcalories.core.dialog.DialogEvent;
import com.github.st1hy.countthemcalories.core.dialog.DialogView;
import com.github.st1hy.countthemcalories.core.rx.Functions;
import com.github.st1hy.countthemcalories.core.rx.Schedulers;
import com.github.st1hy.countthemcalories.core.state.Visibility;
import com.github.st1hy.countthemcalories.core.tokensearch.LastSearchResult;
import com.github.st1hy.countthemcalories.core.tokensearch.SearchResult;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.IngredientTemplateDao;
import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensity;
import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.github.st1hy.countthemcalories.inject.activities.ingredients.fragment.IngredientsFragmentComponent;
import com.github.st1hy.countthemcalories.inject.activities.ingredients.viewholder.IngredientViewHolderComponent;
import com.github.st1hy.countthemcalories.inject.activities.ingredients.viewholder.IngredientViewHolderModule;
import com.l4digital.fastscroll.FastScroller;

import java.util.LinkedList;
import java.util.Queue;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

import static com.github.st1hy.countthemcalories.activities.ingredients.fragment.model.IngredientOptions.from;

@PerFragment
public class IngredientsDaoAdapter extends CursorRecyclerViewAdapter<AbstractIngredientsViewHolder>
        implements IngredientViewHolder.Callback, FastScroller.SectionIndexer {

    @LayoutRes
    private static final int ingredient_item_layout = R.layout.ingredients_item_scrolling;
    @LayoutRes
    private static final int space_layout = R.layout.list_item_bottom;
    private static final int space_layout_top = R.layout.list_item_top;
    private static final int SPACE_TOP = 1;
    private static final int SPACE_BOTTOM = 1;
    private static final int SPACE = 2;

    @NonNull
    private final IngredientsView view;
    @NonNull
    private final IngredientsFragmentModel model;
    @NonNull
    private final RxIngredientsDatabaseModel databaseModel;
    @NonNull
    private final IngredientsDatabaseCommands commands;
    @NonNull
    private final LastSearchResult recentSearchResult;
    @NonNull
    private final Observable<SearchResult> searchResultObservable;
    @NonNull
    private final DialogView dialogView;
    @NonNull
    private final IngredientsFragmentComponent component;

    private final Queue<Long> addedItems = new LinkedList<>();

    @Inject
    public IngredientsDaoAdapter(@NonNull IngredientsView view,
                                 @NonNull IngredientsFragmentModel model,
                                 @NonNull RxIngredientsDatabaseModel databaseModel,
                                 @NonNull IngredientsDatabaseCommands commands,
                                 @NonNull LastSearchResult recentSearchResult,
                                 @NonNull Observable<SearchResult> searchResultObservable,
                                 @NonNull DialogView dialogView,
                                 @NonNull IngredientsFragmentComponent component) {
        this.view = view;
        this.model = model;
        this.databaseModel = databaseModel;
        this.commands = commands;
        this.recentSearchResult = recentSearchResult;
        this.searchResultObservable = searchResultObservable;
        this.dialogView = dialogView;
        this.component = component;
    }

    @Override
    public void onStart() {
        super.onStart();
        addSubscription(searchIngredients(searchResultObservable));
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
                .newIngredientViewHolderComponent(new IngredientViewHolderModule(view, this));
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

    @Override
    public void onIngredientClicked(@NonNull final IngredientViewHolder viewHolder) {
        final IngredientTemplate ingredientTemplate = viewHolder.getReusableIngredient();
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
                                onEditClicked(viewHolder);
                                break;
                            case REMOVE:
                                onDeleteClicked(viewHolder);
                                break;
                        }
                    });
        }
    }

    @Override
    public void onDeleteClicked(@NonNull IngredientViewHolder viewHolder) {
        viewHolder.setEnabled(false);
        final int positionInAdapter = viewHolder.getPositionInAdapter();
        long id = viewHolder.getReusableIngredient().getId();
        databaseModel.getById(id)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(template -> {
                    if (template.getChildIngredients().isEmpty())
                        return Observable.just(template);
                    else
                        return view.showUsedIngredientRemoveConfirmationDialog()
                                .doOnNext(event -> {
                                    if (event == DialogEvent.DISMISS) {
                                        viewHolder.setEnabled(true);
                                    }
                                })
                                .filter(event -> event == DialogEvent.POSITIVE)
                                .map(Functions.into(template));
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
                            int previousCursorSize = getDaoItemCount();
                            onCursorUpdate(result.getCursor(), recentSearchResult.get());
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
                        })
                ))
                .map(Functions.intoResponse())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cursor -> {
                    int previousCursorSize = getDaoItemCount();
                    onCursorUpdate(cursor, recentSearchResult.get());
                    if (cursor.getCount() > 0) {
                        notifyListenersItemRemove(positionInAdapter);
                        if (previousCursorSize > 1) {
                            notifyItemRemoved(positionInAdapter);
                        } else {
                            notifyDataSetChanged();
                        }
                    } else {
                        notifyDataSetChanged();
                    }
                });
    }

    @Override
    public void onEditClicked(@NonNull IngredientViewHolder viewHolder) {
        long id = viewHolder.getReusableIngredient().getId();
        int positionInAdapter = viewHolder.getPositionInAdapter();
        //Ingredient template here is not attached to database and is missing tags
        databaseModel.getByIdRecursive(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ingredientTemplate1 -> {
                    view.editIngredientTemplate(positionInAdapter, ingredientTemplate1);
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
                            .subscribe(pos -> view.scrollToPosition(positionInAdapter(pos)))
            );
        }
    }

    private static boolean isSuccessful(int cursorPosition) {
        return cursorPosition != -1;
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

    private static int positionInCursor(int positionInAdapter) {
        return positionInAdapter - SPACE_TOP;
    }

    private static int positionInAdapter(int positionInCursor) {
        return positionInCursor + SPACE_TOP;
    }
}
