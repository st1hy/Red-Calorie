package com.github.st1hy.countthemcalories.activities.tags.presenter;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.tags.model.RxTagsDatabaseModel;
import com.github.st1hy.countthemcalories.activities.tags.model.TagsActivityModel;
import com.github.st1hy.countthemcalories.activities.tags.model.commands.TagsDatabaseCommands;
import com.github.st1hy.countthemcalories.activities.tags.model.TagsViewModel;
import com.github.st1hy.countthemcalories.activities.tags.presenter.viewholder.EmptySpaceViewHolder;
import com.github.st1hy.countthemcalories.activities.tags.presenter.viewholder.OnTagInteraction;
import com.github.st1hy.countthemcalories.activities.tags.presenter.viewholder.TagItemViewHolder;
import com.github.st1hy.countthemcalories.activities.tags.presenter.viewholder.TagViewHolder;
import com.github.st1hy.countthemcalories.activities.tags.view.TagsView;
import com.github.st1hy.countthemcalories.core.adapter.RecyclerEvent;
import com.github.st1hy.countthemcalories.core.adapter.RxDaoSearchAdapter;
import com.github.st1hy.countthemcalories.core.command.CommandResponse;
import com.github.st1hy.countthemcalories.core.rx.Functions;
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

public class TagsDaoAdapter extends RxDaoSearchAdapter<TagViewHolder, Tag> implements OnTagInteraction {

    static final int bottomSpaceItem = 1;
    static final int item_layout = R.layout.tags_item;
    static final int item_bottom_space_layout = R.layout.tags_item_bottom_space;

    final TagsView view;
    final RxTagsDatabaseModel databaseModel;
    final TagsActivityModel activityModel;
    final TagsViewModel viewModel;
    final TagsDatabaseCommands commands;

    @Inject
    public TagsDaoAdapter(@NonNull TagsView view,
                          @NonNull RxTagsDatabaseModel databaseModel,
                          @NonNull TagsActivityModel activityModel,
                          @NonNull TagsViewModel viewModel,
                          @NonNull TagsDatabaseCommands commands) {
        super(databaseModel);
        this.view = view;
        this.databaseModel = databaseModel;
        this.activityModel = activityModel;
        this.viewModel = viewModel;
        this.commands = commands;
    }

    @Override
    public void onStart() {
        super.onStart();
        onAddTagClicked(view.getOnAddTagClickedObservable());
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
            return new TagItemViewHolder(view, this);
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
        if (activityModel.isInSelectMode()) {
            view.setResultAndReturn(tag.getId(), tag.getName());
        }
    }

    @Override
    public void onTagLongClicked(final int position, @NonNull final Tag tag) {
        addSubscription(
                view.showRemoveTagDialog()
                        .flatMap(new Func1<Void, Observable<CommandResponse<Cursor, Cursor>>>() {
                            @Override
                            public Observable<CommandResponse<Cursor, Cursor>> call(Void aVoid) {
                                return commands.delete(tag);
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(showUndoRemoval())
                        .map(Functions.<Cursor>intoResponse())
                        .subscribe(onTagRemoved(position))
        );
    }

    @NonNull
    @Override
    public Observable<RecyclerEvent> getEvents() {
        return getEventSubject();
    }

    @NonNull
    private OnCursor onTagRemoved(final int position) {
        return new OnCursor() {
            @Override
            public void onNext(Cursor cursor) {
                super.onNext(cursor);
                getEventSubject().onNext(RecyclerEvent.create(RecyclerEvent.Type.REMOVED, position));
                notifyItemRemoved(position);
            }
        };
    }

    @NonNull
    private Action1<CommandResponse<Cursor, Cursor>> showUndoRemoval() {
        return new Action1<CommandResponse<Cursor, Cursor>>() {
            @Override
            public void call(final CommandResponse<Cursor, Cursor> deleteResponse) {
                addSubscription(deleteResponse.undoAvailability()
                        .compose(onUndoAvailable(deleteResponse, viewModel.getUndoDeleteMessage()))
                        .subscribe(onTagAdded())
                );
            }
        };
    }

    @NonNull
    private Observable.Transformer<Boolean, Cursor> onUndoAvailable(@NonNull final CommandResponse<Cursor, Cursor> response,
                                                                    @StringRes final int undoMessage) {
        return new Observable.Transformer<Boolean, Cursor>() {
            @Override
            public Observable<Cursor> call(@NonNull Observable<Boolean> availability) {
                return availability.observeOn(AndroidSchedulers.mainThread())
                        .flatMap(new Func1<Boolean, Observable<Void>>() {
                            @Override
                            public Observable<Void> call(Boolean isAvailable) {
                                if (isAvailable)
                                    return view.showUndoMessage(undoMessage);
                                else {
                                    view.hideUndoMessage();
                                    return Observable.empty();
                                }
                            }
                        })
                        .flatMap(new Func1<Void, Observable<CommandResponse<Cursor, Cursor>>>() {
                            @Override
                            public Observable<CommandResponse<Cursor, Cursor>> call(Void aVoid) {
                                return response.undo();
                            }
                        }).map(Functions.<Cursor>intoResponse());
            }
        };
    }

    @NonNull
    @Override
    protected Observable<Cursor> getAllWithFilter(@NonNull String filter) {
        Collection<Long> excludedIds = activityModel.getExcludedTagIds();
        if (excludedIds.isEmpty())
            return super.getAllWithFilter(filter);
        else
            return databaseModel.getAllFiltered(filter, excludedIds);
    }

    @Override
    protected void onCursorUpdate(@NonNull Cursor cursor) {
        super.onCursorUpdate(cursor);
        view.setNoTagsButtonVisibility(Visibility.of(cursor.getCount() == 0));
    }

    void onAddTagClicked(@NonNull Observable<Void> clicks) {
        addSubscription(
                clicks.subscribeOn(AndroidSchedulers.mainThread())
                        .retry()
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
                        .flatMap(new Func1<Tag, Observable<CommandResponse<Cursor, Cursor>>>() {
                            @Override
                            public Observable<CommandResponse<Cursor, Cursor>> call(Tag tag) {
                                return commands.insert(tag);
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(showUndoAddition())
                        .map(Functions.<Cursor>intoResponse())
                        .subscribe(onTagAdded())
        );
    }

    @NonNull
    private Action1<CommandResponse<Cursor, Cursor>> showUndoAddition() {
        return new Action1<CommandResponse<Cursor, Cursor>>() {
            @Override
            public void call(CommandResponse<Cursor, Cursor> addResponse) {
                addSubscription(addResponse.undoAvailability()
                        .compose(onUndoAvailable(addResponse, viewModel.getUndoAddMessage()))
                        .subscribe(onTagRemoved(addResponse.getResponse().getPosition()))
                );
            }
        };
    }

    @NonNull
    private OnCursor onTagAdded() {
        return new OnCursor() {
            @Override
            public void onNext(Cursor cursor) {
                super.onNext(cursor);
                int newItemPosition = cursor.getPosition();
                getEventSubject().onNext(RecyclerEvent.create(RecyclerEvent.Type.REMOVED, newItemPosition));
                notifyItemInserted(newItemPosition);
                view.scrollToPosition(newItemPosition);
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
                return view.showEditTextDialog(viewModel.getNewTagDialogTitle());
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

}
