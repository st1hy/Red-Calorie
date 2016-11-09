package com.github.st1hy.countthemcalories.activities.tags.fragment.presenter;

import android.database.Cursor;
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
import com.github.st1hy.countthemcalories.activities.tags.fragment.viewholder.EmptySpaceViewHolder;
import com.github.st1hy.countthemcalories.activities.tags.fragment.viewholder.OnTagInteraction;
import com.github.st1hy.countthemcalories.activities.tags.fragment.viewholder.TagItemViewHolder;
import com.github.st1hy.countthemcalories.activities.tags.fragment.viewholder.TagViewHolder;
import com.github.st1hy.countthemcalories.core.adapter.RecyclerEvent;
import com.github.st1hy.countthemcalories.core.adapter.RxDaoSearchAdapter;
import com.github.st1hy.countthemcalories.core.command.CommandResponse;
import com.github.st1hy.countthemcalories.core.command.InsertResult;
import com.github.st1hy.countthemcalories.core.command.UndoTransformer;
import com.github.st1hy.countthemcalories.core.rx.Functions;
import com.github.st1hy.countthemcalories.core.rx.SimpleSubscriber;
import com.github.st1hy.countthemcalories.core.state.Visibility;
import com.github.st1hy.countthemcalories.database.Tag;
import com.google.common.base.Strings;

import java.util.Collection;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import timber.log.Timber;

public class TagsDaoAdapter extends RxDaoSearchAdapter<TagViewHolder> implements OnTagInteraction {

    static final int bottomSpaceItem = 1;
    static final int item_layout = R.layout.tags_item_scrolling;
    static final int item_bottom_space_layout = R.layout.tags_item_bottom_space;

    final TagsView view;
    final RxTagsDatabaseModel databaseModel;
    final TagsFragmentModel fragmentModel;
    final TagsViewModel viewModel;
    final TagsDatabaseCommands commands;

    @Inject
    public TagsDaoAdapter(@NonNull TagsView view,
                          @NonNull RxTagsDatabaseModel databaseModel,
                          @NonNull TagsFragmentModel fragmentModel,
                          @NonNull TagsViewModel viewModel,
                          @NonNull TagsDatabaseCommands commands) {
        super(databaseModel);
        this.view = view;
        this.databaseModel = databaseModel;
        this.fragmentModel = fragmentModel;
        this.viewModel = viewModel;
        this.commands = commands;
    }

    @Override
    public void onStart() {
        super.onStart();
        onAddTagClicked(view.getAddTagClickedObservable());
        onSearch(view.getQueryObservable());
    }

    @Override
    public int getItemViewType(int position) {
        if (position < getDaoItemCount()) {
            return item_layout;
        } else {
            return item_bottom_space_layout;
        }
    }

    @Override
    public TagViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        if (viewType == item_layout) {
            TagItemViewHolder item = new TagItemViewHolder(view, this);
            item.fillParent(parent);
            return item;
        } else {
            return new EmptySpaceViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(TagViewHolder holder, int position) {
        if (holder instanceof TagItemViewHolder) {
            onBindToTagHolder((TagItemViewHolder) holder, position);
        }
    }

    private void onBindToTagHolder(@NonNull TagItemViewHolder holder, int position) {
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
    public int getItemCount() {
        return super.getItemCount() + bottomSpaceItem;
    }

    @Override
    public void onViewAttachedToWindow(TagViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (holder instanceof TagItemViewHolder) {
            ((TagItemViewHolder) holder).onAttached();
        }
    }

    @Override
    public void onViewDetachedFromWindow(TagViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (holder instanceof TagItemViewHolder) {
            ((TagItemViewHolder) holder).onDetached();
        }
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
                view.showEditTextDialog(viewModel.getEditTagDialogTitle(), tag.getName())
                        .flatMap(updateTag(tag))
                        .map(findNewPositionOf(tag))
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

    @NonNull
    private Func1<Cursor, InsertResult> findNewPositionOf(@NonNull final Tag tag) {
        return new Func1<Cursor, InsertResult>() {
            @Override
            public InsertResult call(Cursor cursor) {
                return new InsertResult(cursor, databaseModel.findInCursor(cursor, tag));
            }
        };
    }

    @NonNull
    private Func1<String, Observable<Cursor>> updateTag(@NonNull final Tag tag) {
        return new Func1<String, Observable<Cursor>>() {
            @Override
            public Observable<Cursor> call(String newName) {
                tag.setName(newName);
                return databaseModel.updateRefresh(tag);
            }
        };
    }

    @Override
    public void onDeleteClicked(final int position, @NonNull final Tag tag) {
        addSubscription(
                databaseModel.getById(tag.getId())
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMap(showWarningIfTagIsUsed())
                        .flatMap(deleteTag(tag))
                        .doOnNext(showUndoRemoval())
                        .map(Functions.<Cursor>intoResponse())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(onTagRemoved(position))
        );
    }

    @NonNull
    private Func1<Void, Observable<CommandResponse<Cursor, InsertResult>>> deleteTag(@NonNull final Tag tag) {
        return new Func1<Void, Observable<CommandResponse<Cursor, InsertResult>>>() {
            @Override
            public Observable<CommandResponse<Cursor, InsertResult>> call(Void aVoid) {
                return commands.delete(tag);
            }
        };
    }

    @NonNull
    private Func1<Tag, Observable<Void>> showWarningIfTagIsUsed() {
        return new Func1<Tag, Observable<Void>>() {
            @Override
            public Observable<Void> call(Tag tag) {
                if (tag.getIngredientTypes().isEmpty())
                    return Observable.just(null);
                else
                    return view.showRemoveTagDialog();
            }
        };
    }

    @NonNull
    @Override
    public Observable<RecyclerEvent> getEvents() {
        return getEventSubject();
    }

    @NonNull
    private SimpleSubscriber<Cursor> onTagRemoved(final int position) {
        return new SimpleSubscriber<Cursor>() {
            @Override
            public void onNext(Cursor cursor) {
                onCursorUpdate(lastQuery, cursor);
                if (position != -1) {
                    notifyItemRemovedRx(position);
                } else {
                    notifyDataSetChanged();
                }
            }
        };
    }

    @NonNull
    private Action1<CommandResponse<Cursor, InsertResult>> showUndoRemoval() {
        return new Action1<CommandResponse<Cursor, InsertResult>>() {
            @Override
            public void call(final CommandResponse<Cursor, InsertResult> deleteResponse) {
                addSubscription(deleteResponse.undoAvailability()
                        .compose(onUndoAvailable(deleteResponse, viewModel.getUndoDeleteMessage()))
                        .subscribe(onTagAdded())
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

    void onAddTagClicked(@NonNull Observable<Void> clicks) {
        addSubscription(
                clicks.subscribeOn(AndroidSchedulers.mainThread())
                        .doOnNext(new Action1<Void>() {
                            @Override
                            public void call(Void aVoid) {
                                Timber.v("Add dao clicked");
                            }
                        })
                        .flatMap(showNewTagDialog())
                        .map(trim())
                        .filter(notEmpty())
                        .map(intoTag())
                        .flatMap(new Func1<Tag, Observable<CommandResponse<InsertResult, Cursor>>>() {
                            @Override
                            public Observable<CommandResponse<InsertResult, Cursor>> call(Tag tag) {
                                return commands.insert(tag);
                            }
                        })
                        .doOnNext(showUndoAddition())
                        .map(Functions.<InsertResult>intoResponse())
                        .retry()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(onTagAdded())
        );
    }

    @NonNull
    private Action1<CommandResponse<InsertResult, Cursor>> showUndoAddition() {
        return new Action1<CommandResponse<InsertResult, Cursor>>() {
            @Override
            public void call(CommandResponse<InsertResult, Cursor> addResponse) {
                addSubscription(addResponse.undoAvailability()
                        .compose(onUndoAvailable(addResponse, viewModel.getUndoAddMessage()))
                        .subscribe(onTagRemoved(addResponse.getResponse().getNewItemPositionInCursor()))
                );
            }
        };
    }

    @NonNull
    private SimpleSubscriber<InsertResult> onTagAdded() {
        return new SimpleSubscriber<InsertResult>() {
            @Override
            public void onNext(InsertResult result) {
                int newItemPosition = result.getNewItemPositionInCursor();
                onCursorUpdate(lastQuery, result.getCursor());
                if (newItemPosition != -1) {
                    notifyItemInsertedRx(newItemPosition);
                    view.scrollToPosition(newItemPosition);
                } else {
                    notifyDataSetChanged();
                }
            }
        };
    }

    @NonNull
    private Func1<String, Tag> intoTag() {
        return new Func1<String, Tag>() {
            @Override
            public Tag call(String tagName) {
                return new Tag(null, tagName);
            }
        };
    }

    @NonNull
    Func1<Void, Observable<String>> showNewTagDialog() {
        return new Func1<Void, Observable<String>>() {
            @Override
            public Observable<String> call(Void aVoid) {
                return view.showEditTextDialog(viewModel.getNewTagDialogTitle(), lastQuery);
            }
        };
    }

    @NonNull
    Func1<String, Boolean> notEmpty() {
        return new Func1<String, Boolean>() {
            @Override
            public Boolean call(String s) {
                return !Strings.isNullOrEmpty(s);
            }
        };
    }

    @NonNull
    Func1<String, String> trim() {
        return new Func1<String, String>() {
            @Override
            public String call(String s) {
                return s.trim();
            }
        };
    }

    @NonNull
    private Func1<Boolean, Observable<Void>> showUndoMessage(@StringRes final int undoMessage) {
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

}
