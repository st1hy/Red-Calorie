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
import com.github.st1hy.countthemcalories.activities.tags.presenter.viewholder.EmptySpaceViewHolder;
import com.github.st1hy.countthemcalories.activities.tags.presenter.viewholder.TagItemViewHolder;
import com.github.st1hy.countthemcalories.activities.tags.presenter.viewholder.TagViewHolder;
import com.github.st1hy.countthemcalories.activities.tags.view.TagsView;
import com.github.st1hy.countthemcalories.core.callbacks.OnItemInteraction;
import com.github.st1hy.countthemcalories.core.state.Visibility;
import com.github.st1hy.countthemcalories.database.Tag;
import com.google.common.base.Strings;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class TagsPresenterImp extends RecyclerView.Adapter<TagViewHolder> implements TagsPresenter,
        OnItemInteraction<Tag> {
    public static int debounceTime = 250;
    static final int bottomSpaceItem = 1;
    static final int item_layout = R.layout.tags_item;
    static final int item_bottom_space_layout = R.layout.tags_item_bottom_space;

    final TagsView view;
    final TagsModel model;
    final TagsActivityModel activityModel;
    final CompositeSubscription subscriptions = new CompositeSubscription();

    Cursor cursor;
    Observable<CharSequence> onSearchObservable;

    @Inject
    public TagsPresenterImp(@NonNull TagsView view,
                            @NonNull TagsModel model,
                            @NonNull TagsActivityModel activityModel) {
        this.view = view;
        this.model = model;
        this.activityModel = activityModel;
    }


    @Override
    public void onStart() {
        Timber.d("Started");
        if (onSearchObservable != null) onSearch(onSearchObservable);
        onAddTagClicked(view.getOnAddTagClickedObservable());
    }

    @Override
    public void onStop() {
        subscriptions.clear();
        closeCursor(true);
    }

    @Override
    public void onSearch(@NonNull Observable<CharSequence> observable) {
        onSearchObservable = observable;
        Timber.d("On search");
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

    void onAddTagClicked(@NonNull Observable<Void> clicks) {
        subscribeToCursor(clicks
                .subscribeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        Timber.v("Add tag clicked");
                    }
                })
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
                })
                .retry());
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
        Cursor cursor = this.cursor;
        if (cursor != null) {
            cursor.moveToPosition(position);
            Tag tag = holder.getReusableTag();
            model.performReadEntity(cursor, tag);
            holder.bind(tag);
        } else {
            Timber.w("Cursor closed duding binding views.");
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
        subscriptions.add(view.showRemoveTagDialog().subscribe(deleteTag(tag)));
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
                        TagsPresenterImp.this.cursor = cursor;
                        notifyDataSetChanged();
                        int newSize = cursor.getCount();
                        view.setNoTagsButtonVisibility(Visibility.of(newSize == 0));
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
                subscribeToCursor(model.removeAndRefresh(tag.getId()));
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

    private void closeCursor(boolean notify) {
        Cursor cursor = this.cursor;
        this.cursor = null;
        if (cursor != null) {
            if (notify) notifyDataSetChanged();
            cursor.close();
        }
    }

    private int getTagCount() {
        return cursor != null ? cursor.getCount() : 0;
    }

}
