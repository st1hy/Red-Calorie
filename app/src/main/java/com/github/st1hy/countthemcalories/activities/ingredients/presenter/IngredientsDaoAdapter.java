package com.github.st1hy.countthemcalories.activities.ingredients.presenter;

import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.ingredients.model.RxIngredientsDatabaseModel;
import com.github.st1hy.countthemcalories.activities.ingredients.model.IngredientsModel;
import com.github.st1hy.countthemcalories.activities.ingredients.model.commands.IngredientsDatabaseCommands;
import com.github.st1hy.countthemcalories.activities.ingredients.presenter.viewholder.EmptySpaceViewHolder;
import com.github.st1hy.countthemcalories.activities.ingredients.presenter.viewholder.IngredientItemViewHolder;
import com.github.st1hy.countthemcalories.activities.ingredients.presenter.viewholder.IngredientViewHolder;
import com.github.st1hy.countthemcalories.activities.ingredients.view.IngredientsView;
import com.github.st1hy.countthemcalories.core.command.InsertResult;
import com.github.st1hy.countthemcalories.core.adapter.RecyclerEvent;
import com.github.st1hy.countthemcalories.core.adapter.RxDaoSearchAdapter;
import com.github.st1hy.countthemcalories.core.command.CommandResponse;
import com.github.st1hy.countthemcalories.core.command.UndoTranformer;
import com.github.st1hy.countthemcalories.core.rx.Functions;
import com.github.st1hy.countthemcalories.core.rx.RxPicasso;
import com.github.st1hy.countthemcalories.core.rx.Schedulers;
import com.github.st1hy.countthemcalories.core.rx.SimpleSubscriber;
import com.github.st1hy.countthemcalories.core.state.Visibility;
import com.github.st1hy.countthemcalories.database.IngredientTemplate;
import com.github.st1hy.countthemcalories.database.parcel.IngredientTypeParcel;
import com.github.st1hy.countthemcalories.database.unit.AmountUnitType;
import com.github.st1hy.countthemcalories.database.unit.EnergyDensity;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import timber.log.Timber;

public class IngredientsDaoAdapter extends RxDaoSearchAdapter<IngredientViewHolder, IngredientTemplate>
        implements IngredientItemViewHolder.Callback {
    static final int bottomSpaceItem = 1;
    @LayoutRes
    static final int item_layout = R.layout.ingredients_item_scrolling;
    @LayoutRes
    static final int item_empty_space_layout = R.layout.ingredients_item_bottom_space;

    final IngredientsView view;
    final IngredientsModel model;
    final RxIngredientsDatabaseModel databaseModel;
    final IngredientsDatabaseCommands commands;
    final Picasso picasso;

    final Queue<Long> addedItems = new LinkedList<>();

    @Inject
    public IngredientsDaoAdapter(@NonNull IngredientsView view,
                                 @NonNull IngredientsModel model,
                                 @NonNull RxIngredientsDatabaseModel databaseModel,
                                 @NonNull IngredientsDatabaseCommands commands,
                                 @NonNull Picasso picasso) {
        super(databaseModel);
        this.view = view;
        this.model = model;
        this.databaseModel = databaseModel;
        this.commands = commands;
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
        ImageView imageView = holder.getImage();
        picasso.cancelRequest(imageView);
        Uri imageUri = ingredient.getImageUri();
        if (imageUri != null && !imageUri.equals(Uri.EMPTY)) {
            RxPicasso.Builder.with(picasso, imageUri)
                    .centerCrop()
                    .fit()
                    .into(imageView);
        } else {
            @DrawableRes int imageRes = ingredient.getAmountType() == AmountUnitType.VOLUME ?
                    R.drawable.ic_fizzy_drink : R.drawable.ic_fork_and_knife_wide;
            imageView.setImageResource(imageRes);
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
                onCursorUpdate(cursor);
                notifyItemRemovedRx(position);
            }
        };
    }

    public void onIngredientAdded(long addedIngredientId) {
        addedItems.offer(addedIngredientId);
    }

    @Override
    protected void onSearchFinished() {
        super.onSearchFinished();
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
        return new UndoTranformer<>(response, showUndoMessage(undoMessage));
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
                onCursorUpdate(result.getCursor());
                if (newItemPosition != -1) {
                    notifyItemInsertedRx(newItemPosition);
                    view.scrollToPosition(newItemPosition);
                } else {
                    notifyDataSetChanged();
                }
            }
        };
    }

}
