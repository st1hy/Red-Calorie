package com.github.st1hy.countthemcalories.activities.tags.presenter;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.st1hy.countthemcalories.R;
import com.github.st1hy.countthemcalories.activities.tags.model.TagsActivityModel;
import com.github.st1hy.countthemcalories.activities.tags.model.TagsModel;
import com.github.st1hy.countthemcalories.activities.tags.presenter.viewholder.EmptySpaceViewHolderSpace;
import com.github.st1hy.countthemcalories.activities.tags.presenter.viewholder.TagItemViewHolder;
import com.github.st1hy.countthemcalories.activities.tags.presenter.viewholder.TagViewHolder;
import com.github.st1hy.countthemcalories.activities.tags.view.TagsView;
import com.github.st1hy.countthemcalories.core.callbacks.OnItemInteraction;
import com.github.st1hy.countthemcalories.core.event.DbProcessing;
import com.github.st1hy.countthemcalories.core.ui.Visibility;
import com.github.st1hy.countthemcalories.database.Tag;
import com.google.common.base.Strings;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Notification;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.internal.operators.OnSubscribeRedo;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class TagsPresenterImp extends RecyclerView.Adapter<TagViewHolder> implements TagsPresenter,
        OnItemInteraction<Tag> {
    static final int bottomSpaceItem = 1;
    static final int item_layout = R.layout.tags_item;
    static final int item_bottom_space_layout = R.layout.tags_item_bottom_space;

    final TagsView view;
    final TagsModel model;
    final TagsActivityModel activityModel;
    final CompositeSubscription subscriptions = new CompositeSubscription();
    final CompositeSubscription viewBindingSubs = new CompositeSubscription();
    private Cursor cursor;

    @Inject
    public TagsPresenterImp(@NonNull TagsView view,
                            @NonNull TagsModel model,
                            @NonNull TagsActivityModel activityModel) {
        this.view = view;
        this.model = model;
        this.activityModel = activityModel;
    }

    @Override
    public void onAddTagClicked(@NonNull Observable<Void> clicks) {
        subscribeToCursor(clicks
                .flatMap(showNewTagDialog())
                .map(trim())
                .filter(notEmpty())
                .flatMap(addTagRefresh())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<Cursor>() {
                    @Override
                    public void call(Cursor cursor) {
                        view.scrollToPosition(cursor.getPosition());
                    }
                }));
    }

    @Override
    public void onStart() {
        subscriptions.add(model.getDbProcessingObservable()
                .subscribe(onDbProcessing()));
        Timber.v("Starting query for all items");
        subscribeToCursor(model.getAllObservable());
    }

    @Override
    public void onStop() {
        viewBindingSubs.clear();
        subscriptions.clear();
        closeCursor();
    }

    @Override
    public void onRefresh(@NonNull Observable<Void> refreshes) {
        refreshes.subscribe(refreshModel());
    }

    @Override
    public void onSearch(@NonNull Observable<CharSequence> observable) {
        Observable<Cursor> listObservable = observable
                .debounce(250, TimeUnit.MILLISECONDS)
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
                });
        Observable<Cursor> onSearch = OnSubscribeRedo.retry(listObservable, REDO_INFINITE,
                AndroidSchedulers.mainThread());
        subscribeToCursor(onSearch);
    }

    @Override
    public int getItemViewType(int position) {
        if (position < getTagCount()) {
            return item_layout;
        } else {
            return item_bottom_space_layout;
        }
    }

    @Override
    public TagViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, null);
        if (viewType == item_layout) {
            view.setLayoutParams(parent.getLayoutParams());
            return new TagItemViewHolder(view, this);
        } else {
            return new EmptySpaceViewHolderSpace(view);
        }
    }

    @Override
    public void onBindViewHolder(TagViewHolder holder, int position) {
        if (holder instanceof TagItemViewHolder) {
            final TagItemViewHolder itemViewHolder = (TagItemViewHolder) holder;
            viewBindingSubs.add(itemViewHolder.bind(getItemAt(position)));
        }
    }

    @Override
    public int getItemCount() {
        return getTagCount() + bottomSpaceItem;
    }

    @Override
    public void onItemClicked(@NonNull Tag tag) {
        if (activityModel.isInSelectMode()) {
            view.setResultAndReturn(tag.getId(), tag.getName());
        }
    }

    @Override
    public void onItemLongClicked(@NonNull Tag tag) {
        view.showRemoveTagDialog().subscribe(deleteTag(tag));
    }

    @NonNull
    Func1<String, Observable<Cursor>> addTagRefresh() {
        return new Func1<String, Observable<Cursor>>() {
            @Override
            public Observable<Cursor> call(String tagName) {
                return model.addNewAndRefresh(new Tag(null, tagName));
            }
        };
    }

    @NonNull
    Action1<DbProcessing> onDbProcessing() {
        return new Action1<DbProcessing>() {
            @Override
            public void call(DbProcessing dbProcessing) {
//                Timber.v("Db processing %s", dbProcessing);
            }
        };
    }

    void subscribeToCursor(@NonNull Observable<Cursor> cursorObservable) {
        subscriptions.add(cursorObservable
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        Timber.v("Db cursor query started");
                        view.setDataRefreshing(true);
                    }
                })
                .subscribe(new Subscriber<Cursor>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "Error when providing cursor");
                        view.setDataRefreshing(false);
                    }

                    @Override
                    public void onNext(Cursor cursor) {
                        Timber.v("Db cursor query ended");
                        viewBindingSubs.clear();
                        closeCursor();
                        TagsPresenterImp.this.cursor = cursor;
                        view.setNoTagsButtonVisibility(Visibility.of(cursor.getCount() == 0));
                        notifyDataSetChanged();
                        view.setDataRefreshing(false);
                    }
                }));
    }

    @NonNull
    Func1<Void, Observable<String>> showNewTagDialog() {
        return new Func1<Void, Observable<String>>() {
            @Override
            public Observable<String> call(Void aVoid) {
                return view.showEditTextDialog(model.getNewTagDialogTitle());
            }
        };
    }

    @NonNull
    Action1<Void> refreshModel() {
        return new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                subscribeToCursor(model.getAllObservable());
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
    Action1<Void> deleteTag(final Tag tag) {
        return new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                subscribeToCursor(model.removeAndRefresh(tag));
            }
        };
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

    static final Func1<Observable<? extends Notification<?>>, Observable<?>> REDO_INFINITE = new Func1<Observable<? extends Notification<?>>, Observable<?>>() {
        @Override
        public Observable<?> call(Observable<? extends Notification<?>> ts) {
            return ts.map(new Func1<Notification<?>, Notification<?>>() {
                @Override
                public Notification<?> call(Notification<?> terminal) {
                    return Notification.createOnNext(null);
                }
            });
        }
    };

    private void closeCursor() {
        Cursor cursor = this.cursor;
        this.cursor = null;
        if (cursor != null) {
            cursor.close();
        }
    }

    private int getTagCount() {
        return cursor != null ? cursor.getCount() : 0;
    }

    private Observable<Tag> getItemAt(int position) {
        if (cursor != null) {
            return model.getFromCursor(cursor, position);
        } else {
            throw new IllegalStateException("Performing query for cursor that no longer exists");
        }
    }
}
