package com.github.st1hy.countthemcalories.activities.tags.fragment.presenter;

import android.database.Cursor;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.tags.fragment.model.RxTagsDatabaseModel;
import com.github.st1hy.countthemcalories.activities.tags.fragment.model.TagsFragmentModel;
import com.github.st1hy.countthemcalories.activities.tags.fragment.model.TagsViewModel;
import com.github.st1hy.countthemcalories.activities.tags.fragment.model.commands.TagsDatabaseCommands;
import com.github.st1hy.countthemcalories.activities.tags.fragment.view.TagsView;
import com.github.st1hy.countthemcalories.activities.tags.fragment.viewholder.OnTagInteraction;
import com.github.st1hy.countthemcalories.activities.tags.fragment.viewholder.TagViewHolder;
import com.github.st1hy.countthemcalories.core.adapter.RecyclerEvent;
import com.github.st1hy.countthemcalories.core.adapter.RxDaoSearchAdapter;
import com.github.st1hy.countthemcalories.core.command.InsertResult;
import com.github.st1hy.countthemcalories.core.command.undo.UndoAction;
import com.github.st1hy.countthemcalories.core.command.undo.UndoTransformer;
import com.github.st1hy.countthemcalories.core.command.undo.UndoView;
import com.github.st1hy.countthemcalories.core.rx.Functions;
import com.github.st1hy.countthemcalories.core.rx.SimpleSubscriber;
import com.github.st1hy.countthemcalories.core.state.Visibility;
import com.github.st1hy.countthemcalories.database.Tag;
import com.github.st1hy.countthemcalories.inject.PerFragment;
import com.google.common.base.Strings;

import java.util.Collection;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import timber.log.Timber;

@PerFragment
public class TagsDaoAdapter extends RxDaoSearchAdapter<TagViewHolder> implements OnTagInteraction {

    private static final int item_layout = R.layout.tags_item_scrolling;

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

    @Override
    public void onStart() {
        super.onStart();
        onAddTagClicked(view.getAddTagClickedObservable());
        onSearch(view.getQueryObservable());
        addSubscription(
                view.firstVisibleElementPosition()
                        .map(position -> position > 0)
                        .distinctUntilChanged()
                        .map(Visibility::of)
                        .subscribe(view::setScrollToTopVisibility)
        );
        addSubscription(
                view.scrollToTop()
                        .subscribe(ignore -> view.scrollToPosition(0))
        );
    }

    @Override
    public TagViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(item_layout, parent, false);
        TagViewHolder item = new TagViewHolder(view, this);
        item.fillParent(parent);
        return item;
    }

    @Override
    public void onBindViewHolder(TagViewHolder holder, int position) {
        Cursor cursor = getCursor();
        if (cursor != null) {
            cursor.moveToPosition(position);
            Tag tag = holder.getReusableTag();
            databaseModel.performReadEntity(cursor, tag);
            holder.bind(position, tag);
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
    public void onTagClicked(int position, @NonNull Tag tag) {
        if (fragmentModel.isInSelectMode()) {
            view.onTagSelected(tag);
        } else {
            view.openIngredientsFilteredBy(tag.getName());
        }
    }

    @Override
    public void onEditClicked(final int position, @NonNull final Tag tag) {
        addSubscription(
                view.newTagDialog(viewModel.getEditTagDialogTitle(), tag.getName())
                        .flatMap(newName -> {
                            tag.setName(newName);
                            return databaseModel.updateRefresh(tag);
                        })
                        .map(cursor -> new InsertResult(cursor, databaseModel.findInCursor(cursor, tag)))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(onEditFinished(position))
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
                    notifyItemRemovedRx(position);
                    notifyItemInsertedRx(newPosition);
                } else {
                    notifyDataSetChanged();
                }
            }
        };
    }

    @Override
    public void onDeleteClicked(final int position, @NonNull final Tag tag) {
        addSubscription(
                databaseModel.getById(tag.getId())
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMap(tag1 -> {
                            if (tag1.getIngredientTypes().isEmpty())
                                return Observable.just(null);
                            else
                                return view.showRemoveTagDialog();
                        })
                        .flatMap(aVoid -> commands.delete(tag))
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
    @Override
    public Observable<RecyclerEvent> getEvents() {
        return getEventSubject();
    }

    @NonNull
    private Action1<Cursor> onTagRemoved(final int position) {
        return cursor -> {
            onCursorUpdate(lastQuery, cursor);
            if (position != -1) {
                notifyItemRemovedRx(position);
            } else {
                notifyDataSetChanged();
            }
        };
    }

    @CheckResult
    @NonNull
    @Override
    protected Observable<Cursor> getAllWithFilter(@NonNull String filter) {
        Collection<String> excludedTags = fragmentModel.getExcludedTagIds();
        return databaseModel.getAllFiltered(filter, excludedTags);
    }

    @Override
    protected void onCursorUpdate(@NonNull String query, @NonNull Cursor cursor) {
        super.onCursorUpdate(query, cursor);
        view.setNoTagsMessage(query.trim().isEmpty() ? viewModel.getNoTagsMessage() :
                viewModel.getSearchResultEmptyMessage());
        view.setNoTagsVisibility(Visibility.of(cursor.getCount() == 0));
    }

    private void onAddTagClicked(@NonNull Observable<Void> clicks) {
        addSubscription(
                clicks.subscribeOn(AndroidSchedulers.mainThread())
                        .doOnNext(aVoid -> Timber.v("Add dao clicked"))
                        .flatMap(aVoid1 -> view.newTagDialog(
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
            onCursorUpdate(lastQuery, result.getCursor());
            if (newItemPosition != -1) {
                notifyItemInsertedRx(newItemPosition);
                view.scrollToPosition(newItemPosition);
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

}
