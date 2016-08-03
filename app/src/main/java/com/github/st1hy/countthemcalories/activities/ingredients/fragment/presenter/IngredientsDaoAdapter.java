package com.github.st1hy.countthemcalories.activities.ingredients.fragment.presenter;

import android.database.Cursor;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.model.IngredientOptions;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.model.IngredientsFragmentModel;
import com.github.st1hy.countthemcalories.core.tokensearch.LastSearchResult;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.view.IngredientsView;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.viewholder.EmptySpaceViewHolder;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.viewholder.IngredientItemViewHolder;
import com.github.st1hy.countthemcalories.activities.ingredients.fragment.viewholder.IngredientViewHolder;
import com.github.st1hy.countthemcalories.activities.ingredients.model.RxIngredientsDatabaseModel;
import com.github.st1hy.countthemcalories.activities.ingredients.model.commands.IngredientsDatabaseCommands;
import com.github.st1hy.countthemcalories.core.adapter.CursorRecyclerViewAdapter;
import com.github.st1hy.countthemcalories.core.adapter.RecyclerEvent;
import com.github.st1hy.countthemcalories.core.command.CommandResponse;
import com.github.st1hy.countthemcalories.core.command.InsertResult;
import com.github.st1hy.countthemcalories.core.command.UndoTransformer;
import com.github.st1hy.countthemcalories.core.permissions.PermissionsHelper;
import com.github.st1hy.countthemcalories.core.rx.Functions;
import com.github.st1hy.countthemcalories.core.rx.Schedulers;
import com.github.st1hy.countthemcalories.core.rx.SimpleSubscriber;
import com.github.st1hy.countthemcalories.core.state.Visibility;
import com.github.st1hy.countthemcalories.core.tokensearch.SearchResult;
import com.github.st1hy.countthemcalories.core.withpicture.imageholder.ImageHolderDelegate;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.parcel.IngredientTypeParcel;
import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensity;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import timber.log.Timber;

import static com.github.st1hy.countthemcalories.activities.ingredients.fragment.model.IngredientOptions.from;

public class IngredientsDaoAdapter extends CursorRecyclerViewAdapter<IngredientViewHolder>
        implements IngredientItemViewHolder.Callback {

    static final int bottomSpaceItem = 1;
    @LayoutRes
    static final int item_layout = R.layout.ingredients_item_scrolling;
    @LayoutRes
    static final int item_empty_space_layout = R.layout.ingredients_item_bottom_space;

    final IngredientsView view;
    final IngredientsFragmentModel model;
    final RxIngredientsDatabaseModel databaseModel;
    final IngredientsDatabaseCommands commands;
    final Picasso picasso;
    final PermissionsHelper permissionsHelper;

    final Queue<Long> addedItems = new LinkedList<>();

    final LastSearchResult recentSearchResult;

    public IngredientsDaoAdapter(@NonNull IngredientsView view,
                                 @NonNull IngredientsFragmentModel model,
                                 @NonNull RxIngredientsDatabaseModel databaseModel,
                                 @NonNull IngredientsDatabaseCommands commands,
                                 @NonNull Picasso picasso,
                                 @NonNull PermissionsHelper permissionsHelper,
                                 @NonNull LastSearchResult recentSearchResult) {
        this.view = view;
        this.model = model;
        this.databaseModel = databaseModel;
        this.commands = commands;
        this.picasso = picasso;
        this.permissionsHelper = permissionsHelper;
        this.recentSearchResult = recentSearchResult;
    }

    @Override
    public void onStart() {
        super.onStart();
        addSubscription(searchIngredients(view.getSearchObservable()));
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
                                    int position) {
        if (model.isInSelectMode()) {
            view.onIngredientSelected(new IngredientTypeParcel(ingredientTemplate));
        } else {
            view.showAlertDialog(model.getIngredientOptionsTitle(), model.getIngredientOptions())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(onAddToNewMealClicked(ingredientTemplate, position));
        }
    }

    @Override
    public void onDeleteClicked(@NonNull IngredientTemplate ingredientTemplate, final int position) {
        databaseModel.getById(ingredientTemplate.getId())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(showConfirmationIfUsed())
                .flatMap(deleteIngredient())
                .doOnNext(showUndoRemoval())
                .map(Functions.<Cursor>intoResponse())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onItemRemoved(position));
    }

    @Override
    public void onEditClicked(@NonNull IngredientTemplate ingredientTemplate, int position) {
        view.openEditIngredientScreen(position, new IngredientTypeParcel(ingredientTemplate));
    }

    @NonNull
    @Override
    public Observable<RecyclerEvent> getEvents() {
        return getEventSubject();
    }

    @NonNull
    private Subscription searchIngredients(@NonNull Observable<SearchResult> sequenceObservable) {
        return sequenceObservable
                .flatMap(queryDatabaseFiltered())
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable e) {
                        Timber.e(e, "Search exploded");
                    }
                })
                .retry(128)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleSubscriber<QueryFinished>() {
                    @Override
                    public void onNext(QueryFinished queryFinished) {
                        SearchResult searchResult = queryFinished.getSearchingFor();
                        recentSearchResult.set(searchResult);
                        onCursorUpdate(queryFinished.getCursor(), searchResult);
                        notifyDataSetChanged();
                        onSearchFinished();
                    }
                });
    }

    @NonNull
    private Subscriber<Integer> onAddToNewMealClicked(@NonNull final IngredientTemplate ingredient,
                                                      final int position) {
        return new SimpleSubscriber<Integer>() {
            @Override
            public void onNext(Integer selectedOptionPosition) {
                IngredientOptions selectedOption = from(selectedOptionPosition);
                switch (selectedOption) {
                    case ADD_TO_NEW:
                        view.openNewMealScreen(new IngredientTypeParcel(ingredient));
                        break;
                    case EDIT:
                        onEditClicked(ingredient, position);
                        break;
                    case REMOVE:
                        onDeleteClicked(ingredient, position);
                        break;
                }
            }
        };
    }

    @NonNull
    private Func1<SearchResult, Observable<QueryFinished>> queryDatabaseFiltered() {
        return new Func1<SearchResult, Observable<QueryFinished>>() {
            @Override
            public Observable<QueryFinished> call(SearchResult searchResult) {
                Observable<Cursor> cursor = getAllWithFilter(searchResult);
                return cursor.map(withSearchResult(searchResult));
            }
        };
    }


    @NonNull
    protected Observable<Cursor> getAllWithFilter(@NonNull SearchResult searchResult) {
        return databaseModel.getAllFilteredBy(searchResult.getQuery(), searchResult.getTokens());
    }


    @NonNull
    private Func1<Cursor, QueryFinished> withSearchResult(@NonNull final SearchResult searchResult) {
        return new Func1<Cursor, QueryFinished>() {
            @Override
            public QueryFinished call(Cursor cursor) {
                return new QueryFinished(cursor, searchResult);
            }
        };
    }

    protected void onCursorUpdate(@NonNull Cursor cursor, @NonNull SearchResult searchingFor) {
        onCursorUpdate(cursor);
        boolean isSearchFilterEmpty = searchingFor.getQuery().trim().isEmpty() && searchingFor.getTokens().isEmpty();
        view.setNoIngredientsMessage(isSearchFilterEmpty ? model.getNoIngredientsMessage() : model.getSearchEmptyMessage());
        view.setNoIngredientsVisibility(Visibility.of(cursor.getCount() == 0));
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

    @NonNull
    private Action1<Cursor> onItemRemoved(final int position) {
        return new Action1<Cursor>() {
            @Override
            public void call(Cursor cursor) {
                onCursorUpdate(cursor, recentSearchResult.get());
                notifyItemRemovedRx(position);
            }
        };
    }

    public void onIngredientAdded(long addedIngredientId) {
        addedItems.offer(addedIngredientId);
    }

    protected void onSearchFinished() {
        final Long newItemId = addedItems.poll();
        if (newItemId != null) {
            addSubscription(Observable.fromCallable(findInCursor(newItemId))
                    .subscribeOn(Schedulers.computation())
                    .filter(findSuccessful())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SimpleSubscriber<Integer>() {
                        @Override
                        public void onNext(Integer integer) {
                            //Add maybe some sort of animation to highlight new item
                            view.scrollToPosition(integer);
                        }
                    }));
        }
    }

    @NonNull
    private Callable<Integer> findInCursor(final long newItemId) {
        return new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return databaseModel.findInCursor(getCursor(), newItemId);
            }
        };
    }

    @NonNull
    private Func1<Integer, Boolean> findSuccessful() {
        return new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer integer) {
                return integer != -1;
            }
        };
    }

    @NonNull
    private Func1<IngredientTemplate, Observable<CommandResponse<Cursor, InsertResult>>> deleteIngredient() {
        return new Func1<IngredientTemplate, Observable<CommandResponse<Cursor, InsertResult>>>() {
            @Override
            public Observable<CommandResponse<Cursor, InsertResult>> call(IngredientTemplate ingredientTemplate) {
                return commands.delete(ingredientTemplate);
            }
        };
    }

    @NonNull
    private Action1<CommandResponse<Cursor, InsertResult>> showUndoRemoval() {
        return new Action1<CommandResponse<Cursor, InsertResult>>() {
            @Override
            public void call(final CommandResponse<Cursor, InsertResult> deleteResponse) {
                addSubscription(deleteResponse.undoAvailability()
                        .compose(onUndoAvailable(deleteResponse, model.getUndoDeleteMessage()))
                        .subscribe(onIngredientAdded())
                );
            }
        };
    }

    @NonNull
    private <Response, UndoResponse> Observable.Transformer<Boolean, UndoResponse> onUndoAvailable(
            @NonNull final CommandResponse<Response, UndoResponse> response,
            @StringRes final int undoMessage) {
        return new UndoTransformer<>(response, showUndoMessage(undoMessage));
    }

    @NonNull
    Func1<Boolean, Observable<Void>> showUndoMessage(@StringRes final int undoMessage) {
        return new Func1<Boolean, Observable<Void>>() {
            @Override
            public Observable<Void> call(Boolean isAvailable) {
                if (isAvailable)
                    return view.showUndoMessage(undoMessage);
                else {
                    view.hideUndoMessage();
                    return Observable.empty();
                }
            }
        };
    }

    @NonNull
    SimpleSubscriber<InsertResult> onIngredientAdded() {
        return new SimpleSubscriber<InsertResult>() {
            @Override
            public void onNext(InsertResult result) {
                int newItemPosition = result.getNewItemPositionInCursor();
                onCursorUpdate(result.getCursor(), recentSearchResult.get());
                if (newItemPosition != -1) {
                    notifyItemInsertedRx(newItemPosition);
                    view.scrollToPosition(newItemPosition);
                } else {
                    notifyDataSetChanged();
                }
            }
        };
    }

    void onBindToIngredientHolder(@NonNull IngredientItemViewHolder holder, int position) {
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

    void onBindImage(@NonNull IngredientTemplate ingredient, @NonNull IngredientItemViewHolder holder) {
        holder.setImagePlaceholder(ingredient.getAmountType() == AmountUnitType.VOLUME ?
                R.drawable.ic_fizzy_drink : R.drawable.ic_fork_and_knife_wide);
        holder.setImageUri(ImageHolderDelegate.from(ingredient.getImageUri()));
    }

    static class QueryFinished {
        final Cursor cursor;
        final SearchResult searchingFor;

        public QueryFinished(@NonNull Cursor cursor, @NonNull SearchResult searchingFor) {
            this.cursor = cursor;
            this.searchingFor = searchingFor;
        }

        @NonNull
        public Cursor getCursor() {
            return cursor;
        }

        @NonNull
        public SearchResult getSearchingFor() {
            return searchingFor;
        }
    }

}
