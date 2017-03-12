package com.github.st1hy.countthemcalories.activities.tags.fragment.adapter;

import android.database.Cursor;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.tags.fragment.adapter.holder.OnTagInteraction;
import com.github.st1hy.countthemcalories.activities.tags.fragment.adapter.holder.TagItemHolder;
import com.github.st1hy.countthemcalories.activities.tags.fragment.adapter.holder.TagViewHolder;
import com.github.st1hy.countthemcalories.activities.tags.fragment.adapter.inject.TagComponent;
import com.github.st1hy.countthemcalories.activities.tags.fragment.adapter.inject.TagComponentFactory;
import com.github.st1hy.countthemcalories.activities.tags.fragment.adapter.inject.TagModule;
import com.github.st1hy.countthemcalories.activities.tags.fragment.model.ColorGenerator;
import com.github.st1hy.countthemcalories.activities.tags.fragment.model.RxTagsDatabaseModel;
import com.github.st1hy.countthemcalories.activities.tags.fragment.model.TagsFragmentModel;
import com.github.st1hy.countthemcalories.activities.tags.fragment.model.TagsViewModel;
import com.github.st1hy.countthemcalories.activities.tags.fragment.model.commands.TagsDatabaseCommands;
import com.github.st1hy.countthemcalories.activities.tags.fragment.view.TagsView;
import com.github.st1hy.countthemcalories.core.adapter.RxDaoSearchAdapter;
import com.github.st1hy.countthemcalories.core.baseview.Click;
import com.github.st1hy.countthemcalories.core.command.InsertResult;
import com.github.st1hy.countthemcalories.core.command.undo.UndoAction;
import com.github.st1hy.countthemcalories.core.command.undo.UndoTransformer;
import com.github.st1hy.countthemcalories.core.command.undo.UndoView;
import com.github.st1hy.countthemcalories.core.dialog.DialogEvent;
import com.github.st1hy.countthemcalories.core.rx.Functions;
import com.github.st1hy.countthemcalories.core.rx.SimpleSubscriber;
import com.github.st1hy.countthemcalories.core.state.Visibility;
import com.github.st1hy.countthemcalories.database.Tag;
import com.github.st1hy.countthemcalories.database.TagDao;
import com.github.st1hy.countthemcalories.database.property.CreationSource;
import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.google.common.base.Strings;
import com.l4digital.fastscroll.FastScroller;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

@PerFragment
public class TagsDaoAdapter extends RxDaoSearchAdapter<TagViewHolder> implements OnTagInteraction,
        FastScroller.SectionIndexer {

    private static final int item_layout = R.layout.tags_item_scrolling;
    private static final int space_bottom = R.layout.list_item_bottom;
    private static final int space_top = R.layout.list_item_top;
    private static final int TOP_ITEM_PADDING = 1;
    private static final int BOTTOM_ITEM_PADDING = 1;
    private static final int ADDITIONAL_ITEMS = BOTTOM_ITEM_PADDING + TOP_ITEM_PADDING;

    @NonNull
    private final TagsView view;
    @NonNull
    private final RxTagsDatabaseModel databaseModel;
    @NonNull
    private final TagsFragmentModel fragmentModel;
    @NonNull
    private final TagsViewModel viewModel;
    @NonNull
    private final TagsDatabaseCommands commands;
    @NonNull
    private final UndoView undoView;
    @Inject
    ColorGenerator colorGenerator;
    @Inject
    TagComponentFactory tagComponentFactory;
    @Inject
    PublishSubject<TagViewHolder> stateChanges;

    private final CompositeSubscription subscriptions = new CompositeSubscription();

    @Inject
    public TagsDaoAdapter(@NonNull TagsView view,
                          @NonNull RxTagsDatabaseModel databaseModel,
                          @NonNull TagsFragmentModel fragmentModel,
                          @NonNull TagsViewModel viewModel,
                          @NonNull TagsDatabaseCommands commands,
                          @NonNull UndoView undoView) {
        super(databaseModel);
        this.view = view;
        this.databaseModel = databaseModel;
        this.fragmentModel = fragmentModel;
        this.viewModel = viewModel;
        this.commands = commands;
        this.undoView = undoView;
    }

    public void onStart() {
        onAddTagClicked(view.addTagClickedObservable());
        addSubscription(
                view.getQueryObservable()
                        .compose(searchDatabase())
                        .subscribe()
        );
        addSubscription(
                stateChanges
                        .filter(holder -> holder instanceof TagItemHolder)
                        .cast(TagItemHolder.class)
                        .subscribe(this::onViewHolderStateChanged)
        );
        view.setConfirmButtonVisibility(Visibility.of(fragmentModel.isInSelectMode()));
        addSubscription(
                view.confirmClickedObservable()
                        .subscribe(click -> view.onTagsSelected(fragmentModel.getTags()))
        );
    }

    public void onStop() {
        subscriptions.clear();
        closeCursor(true);
    }

    @CallSuper
    protected void addSubscription(@NonNull Subscription subscription) {
        subscriptions.add(subscription);
    }

    @Override
    public int getItemViewType(int position) {
        if (position < TOP_ITEM_PADDING) {
            return space_top;
        } else if (position < getDaoItemCount() + TOP_ITEM_PADDING) {
            return item_layout;
        } else {
            return space_bottom;
        }
    }

    @Override
    public TagViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        TagComponent tagComponent = tagComponentFactory.newTagComponent(new TagModule(view));
        if (viewType == item_layout) {
            TagItemHolder itemHolder = tagComponent.tagItemHolder();
            itemHolder.fillParent(parent);
            return itemHolder;
        } else {
            return tagComponent.tagSpaceHolder();
        }
    }

    @Override
    public void onBindViewHolder(TagViewHolder holder, int position) {
        if (holder instanceof TagItemHolder) {
            onBindItemHolder((TagItemHolder) holder, position);
        }
    }

    private void onBindItemHolder(TagItemHolder holder, int position) {
        Cursor cursor = getCursor();
        if (cursor != null) {
            cursor.moveToPosition(positionInCursor(position));
            Tag tag = holder.getReusableTag();
            holder.setEnabled(true);
            databaseModel.performReadEntity(cursor, tag);
            holder.bind(position, tag);
            holder.setSelectable(fragmentModel.isInSelectMode());
            holder.setChecked(fragmentModel.isSelected(tag));
            if (!fragmentModel.isInSelectMode()) {
                holder.setCheckedTint(colorGenerator.getColorFor(tag.getId()));
            }
        } else {
            Timber.w("Cursor closed duding binding views.");
        }
    }

    @Override
    public void onViewAttachedToWindow(TagViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.onAttached();
    }

    @Override
    public void onViewDetachedFromWindow(TagViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.onDetached();
    }

    @Override
    public void onTagClicked(int position, @NonNull TagItemHolder holder) {
        Tag tag = holder.getReusableTag();
        if (fragmentModel.isInSelectMode()) {
            boolean isChecked = !holder.isChecked();
            holder.setChecked(isChecked);
            fragmentModel.setSelected(tag, isChecked);
        } else {
            view.openIngredientsFilteredBy(tag.getDisplayName());
        }
    }

    @Override
    public void onEditClicked(final int positionInAdapter, @NonNull TagItemHolder holder) {
        Tag tag = holder.getReusableTag();
        addSubscription(
                view.newTagDialog(viewModel.getEditTagDialogTitle(), tag.getDisplayName())
                        .flatMap(newName -> {
                            tag.setName(newName);
                            tag.setCreationSource(CreationSource.USER);
                            return databaseModel.updateRefresh(tag);
                        })
                        .map(cursor -> new InsertResult(cursor, databaseModel.findInCursor(cursor, tag)))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(onEditFinished(positionInAdapter))
        );
    }

    @NonNull
    private SimpleSubscriber<InsertResult> onEditFinished(final int position) {
        return new SimpleSubscriber<InsertResult>() {
            @Override
            public void onNext(InsertResult result) {
                onCursorUpdate(lastQuery, result.getCursor());
                int newPosition = result.getNewItemPositionInCursor();
                if (newPosition != -1) {
                    newPosition = positionInAdapter(newPosition);
                    notifyListenersItemRemove(position);
                    if (newPosition != position) notifyItemRemoved(position);
                    notifyListenersItemInserted(newPosition);
                    if (newPosition != position) {
                        notifyItemInserted(newPosition);
                    } else {
                        notifyItemChanged(position);
                    }
                } else {
                    notifyDataSetChanged();
                }
            }
        };
    }

    @Override
    public int getItemCount() {
        int daoItemCount = getDaoItemCount();
        return daoItemCount > 0 ? daoItemCount + ADDITIONAL_ITEMS : 0;
    }

    @Override
    public void onDeleteClicked(final int position, @NonNull TagItemHolder holder) {
        long tagId = holder.getReusableTag().getId();
        holder.setEnabled(false);
        addSubscription(
                databaseModel.getById(tagId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMap(tag -> {
                            if (tag.getIngredientTypes().isEmpty())
                                return Observable.just(tag);
                            else
                                return view.showRemoveTagDialog()
                                        .doOnNext(event -> {
                                            if (event == DialogEvent.DISMISS) {
                                                holder.setEnabled(true);
                                            }
                                        })
                                        .filter(event -> event == DialogEvent.POSITIVE)
                                        .map(Functions.into(tag));
                        })
                        .flatMap(commands::delete)
                        .doOnNext(deleteResponse ->
                                addSubscription(deleteResponse.undoAvailability()
                                        .compose(new UndoTransformer<>(deleteResponse,
                                                showUndoMessage(viewModel.getUndoDeleteMessage()))
                                        )
                                        .subscribe(onTagAdded())
                                ))
                        .map(Functions.intoResponse())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(onTagRemoved(position))
        );
    }

    @NonNull
    private Action1<Cursor> onTagRemoved(final int positionInAdapter) {
        return cursor -> {
            int previousCursorSize = getDaoItemCount();
            onCursorUpdate(lastQuery, cursor);
            if (positionInAdapter != -1) {
                notifyListenersItemRemove(positionInAdapter);
                if (previousCursorSize > 1) {
                    notifyItemRemoved(positionInAdapter);
                } else {
                    notifyDataSetChanged();
                }
            } else {
                notifyDataSetChanged();
            }
        };
    }

    private void onCursorUpdate(@NonNull String lastQuery, @NonNull Cursor cursor) {
        onCursorUpdate(QueryResult.of(lastQuery, cursor));
    }

    @Override
    protected void onCursorUpdate(@NonNull QueryResult result) {
        super.onCursorUpdate(result);
        String query = result.getQuery();
        Cursor cursor = result.getCursor();
        view.setNoTagsMessage(query.trim().isEmpty() ? viewModel.getNoTagsMessage() :
                viewModel.getSearchResultEmptyMessage());
        view.setNoTagsVisibility(Visibility.of(cursor.getCount() == 0));
    }

    private void onAddTagClicked(@NonNull Observable<Click> clicks) {
        addSubscription(
                clicks.subscribeOn(AndroidSchedulers.mainThread())
                        .flatMap(click -> view.newTagDialog(
                                viewModel.getNewTagDialogTitle(), lastQuery)
                        )
                        .map(String::trim)
                        .filter(s -> !Strings.isNullOrEmpty(s))
                        .map(tagName -> new Tag(null, tagName))
                        .flatMap(commands::insert)
                        .doOnNext(addResponse ->
                                addSubscription(addResponse.undoAvailability()
                                        .compose(new UndoTransformer<>(addResponse,
                                                showUndoMessage(viewModel.getUndoAddMessage())))
                                        .subscribe(onTagRemoved(addResponse.getResponse()
                                                .getNewItemPositionInCursor()))
                                ))
                        .map(Functions.intoResponse())
                        .retry()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(onTagAdded())
        );
    }

    @NonNull
    private Action1<InsertResult> onTagAdded() {
        return result -> {
            int newItemPosition = result.getNewItemPositionInCursor();
            int previousCursorSize = getDaoItemCount();
            onCursorUpdate(lastQuery, result.getCursor());
            if (newItemPosition != -1) {
                newItemPosition = positionInAdapter(newItemPosition);
                notifyListenersItemInserted(newItemPosition);
                if (previousCursorSize > 0) {
                    notifyItemInserted(newItemPosition);
                    view.scrollToPosition(newItemPosition);
                } else {
                    notifyDataSetChanged();
                }
            } else {
                notifyDataSetChanged();
            }
        };
    }

    @NonNull
    private Func1<Boolean, Observable<UndoAction>> showUndoMessage(@StringRes final int undoMessage) {
        return isAvailable -> {
            if (isAvailable)
                return undoView.showUndoMessage(undoMessage);
            else {
                undoView.hideUndoMessage();
                return Observable.empty();
            }
        };
    }

    @Override
    public String getSectionText(int position) {
        Cursor cursor = getCursor();
        if (cursor != null) {
            position = positionInCursor(position);
            if (position >= getDaoItemCount()) {
                position = getDaoItemCount() - 1;
            } else if (position < 0) {
                position = 0;
            }
            cursor.moveToPosition(position);
            String string = cursor.getString(cursor.getColumnIndexOrThrow(TagDao.Properties.Name.columnName));
            return string.substring(0, 1);
        } else {
            return "";
        }
    }

    private void onViewHolderStateChanged(TagItemHolder tagItemHolder) {
        Tag reusableTag = tagItemHolder.getReusableTag();
        boolean isChecked = tagItemHolder.isChecked();
        fragmentModel.setSelected(reusableTag, isChecked);
    }

    private static int positionInCursor(int positionInAdapter) {
        return positionInAdapter - TOP_ITEM_PADDING;
    }

    private static int positionInAdapter(int positionInCursor) {
        return positionInCursor + TOP_ITEM_PADDING;
    }
}
